package com.xyz.movie_booking.service;

import com.xyz.movie_booking.dto.BookingRequest;
import com.xyz.movie_booking.dto.BookingResponse;
import com.xyz.movie_booking.exception.ResourceNotFoundException;
import com.xyz.movie_booking.exception.SeatAlreadyBookedException;
import com.xyz.movie_booking.exception.SeatLockException;
import com.xyz.movie_booking.model.entity.Booking;
import com.xyz.movie_booking.model.entity.BookingSeat;
import com.xyz.movie_booking.model.entity.Show;
import com.xyz.movie_booking.model.entity.ShowSeat;
import com.xyz.movie_booking.model.enums.BookingStatus;
import com.xyz.movie_booking.model.enums.SeatStatus;
import com.xyz.movie_booking.repository.BookingRepository;
import com.xyz.movie_booking.repository.BookingSeatRepository;
import com.xyz.movie_booking.repository.ShowRepository;
import com.xyz.movie_booking.repository.ShowSeatRepository;
import com.xyz.movie_booking.strategy.discount.DiscountHandler;
import com.xyz.movie_booking.strategy.pricing.PricingStrategy;
import jakarta.persistence.LockTimeoutException;
import jakarta.persistence.PessimisticLockException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;
import java.util.Map;

@Service
public class BookingService {

    private static final Logger log = LoggerFactory.getLogger(BookingService.class);

    private final PricingStrategy pricingStrategy;
    private final DiscountHandler discountHandler;
    private final ShowRepository showRepository;
    private final ShowSeatRepository showSeatRepository;
    private final BookingRepository bookingRepository;
    private final BookingSeatRepository bookingSeatRepository;
    private final PaymentService paymentService;

    public BookingService(PricingStrategy pricingStrategy,
                          DiscountHandler discountHandler,
                          ShowRepository showRepository,
                          ShowSeatRepository showSeatRepository,
                          BookingRepository bookingRepository,
                          BookingSeatRepository bookingSeatRepository,
                          PaymentService paymentService) {
        this.pricingStrategy = pricingStrategy;
        this.discountHandler = discountHandler;
        this.showRepository = showRepository;
        this.showSeatRepository = showSeatRepository;
        this.bookingRepository = bookingRepository;
        this.bookingSeatRepository = bookingSeatRepository;
        this.paymentService = paymentService;
    }

    public BookingResponse getBooking(Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        List<String> seats = bookingSeatRepository.findByBookingId(bookingId)
                .stream()
                .map(BookingSeat::getSeatNumber)
                .toList();

        return new BookingResponse(
                booking.getId(),
                booking.getStatus(),
                booking.getTotalAmount(),
                "/bookings/" + booking.getId(),
                seats
        );
    }

    @Transactional
    public BookingResponse createBooking(BookingRequest request) {

        Long showId = request.getShowId();
        List<String> requestedSeats = normalizeSeats(request.getSeatNumbers());

        Show show = getShow(showId);

        List<ShowSeat> seats = lockSeats(showId, requestedSeats);

        validateSeats(seats, requestedSeats);

        validateAvailability(show, seats.size());

        log.info("Booking seats={} for showId={}", seats, showId);

        double totalAmount = calculateTotal(seats, show);

        totalAmount = discountHandler.apply(totalAmount, request);

        // LOCK seats
        markSeatsLocked(seats);

        // create booking as PENDING
        Booking booking = createBookingEntity(request, show, seats.size(), totalAmount);

        Booking savedBooking = bookingRepository.save(booking);

        saveBookingSeats(savedBooking, seats);

        // trigger payment AFTER COMMIT
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                paymentService.processPayment(savedBooking.getId());
            }
        });

        return new BookingResponse(
                savedBooking.getId(),
                savedBooking.getStatus(),
                savedBooking.getTotalAmount(),
                "bookings/" + savedBooking.getId(),
                requestedSeats
        );
    }

    private void markSeatsLocked(List<ShowSeat> seats) {
        for (ShowSeat seat : seats) {
            seat.setStatus(SeatStatus.LOCKED);
        }
    }

    private List<String> normalizeSeats(List<String> seats) {
        return seats.stream()
                .map(s -> s.trim().toUpperCase())
                .sorted()
                .toList();
    }

    private Show getShow(Long showId) {
        return showRepository.findById(showId)
                .orElseThrow(() -> new ResourceNotFoundException("Show not found"));
    }

    private List<ShowSeat> lockSeats(Long showId, List<String> requestedSeats) {
        try {
            return showSeatRepository.findSeatsForUpdate(showId, requestedSeats);
        } catch (PessimisticLockException | LockTimeoutException ex) {
            throw new SeatLockException("Seats are temporarily locked. Please retry.");
        }
    }

    private void validateSeats(List<ShowSeat> seats, List<String> requestedSeats) {
        if (seats.size() != requestedSeats.size()) {
            throw new IllegalArgumentException("Some seats are invalid");
        }

        for (ShowSeat seat : seats) {
            if (seat.getStatus() != SeatStatus.AVAILABLE) {
                throw new SeatAlreadyBookedException("Seat already booked: " + seat.getSeatNumber());
            }
        }
    }

    private void validateAvailability(Show show, int requestedCount) {
        if (show.getAvailableSeats() < requestedCount) {
            throw new IllegalArgumentException("Not enough seats available");
        }
    }

    private double calculateTotal(List<ShowSeat> seats, Show show) {

        Map<String, Double> priceMap =
                pricingStrategy.calculateSeatPrices(seats, show);

        double totalAmount = priceMap.values()
                .stream()
                .mapToDouble(Double::doubleValue)
                .sum();

        return totalAmount;
    }

    private Booking createBookingEntity(BookingRequest request, Show show, int seatCount, double totalAmount) {
        Booking booking = new Booking();
        booking.setUserId(request.getUserId());
        booking.setShow(show);
        booking.setNumberOfSeats(seatCount);
        booking.setStatus(BookingStatus.PENDING);
        booking.setTotalAmount(totalAmount);
        return booking;
    }

    private void saveBookingSeats(Booking booking, List<ShowSeat> seats) {
        List<BookingSeat> bookingSeats = seats.stream().map(seat -> {
            BookingSeat bs = new BookingSeat();
            bs.setBooking(booking);
            bs.setSeatNumber(seat.getSeatNumber());
            return bs;
        }).toList();

        bookingSeatRepository.saveAll(bookingSeats);
    }
}