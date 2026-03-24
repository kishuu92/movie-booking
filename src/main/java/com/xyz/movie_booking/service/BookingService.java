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
import jakarta.persistence.LockTimeoutException;
import jakarta.persistence.PessimisticLockException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingService {

    private static final Logger log = LoggerFactory.getLogger(BookingService.class);

    private final ShowRepository showRepository;
    private final ShowSeatRepository showSeatRepository;
    private final BookingRepository bookingRepository;
    private final BookingSeatRepository bookingSeatRepository;

    public BookingService(ShowRepository showRepository,
                          ShowSeatRepository showSeatRepository,
                          BookingRepository bookingRepository,
                          BookingSeatRepository bookingSeatRepository) {
        this.showRepository = showRepository;
        this.showSeatRepository = showSeatRepository;
        this.bookingRepository = bookingRepository;
        this.bookingSeatRepository = bookingSeatRepository;
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

        double totalAmount = calculateTotal(seats);

        markSeatsBooked(seats);

        // Managed entity → auto update via dirty checking
        show.setAvailableSeats(show.getAvailableSeats() - seats.size());

        Booking booking = createBookingEntity(request, show, seats.size(), totalAmount);

        Booking savedBooking = bookingRepository.save(booking);

        saveBookingSeats(savedBooking, seats);

        return new BookingResponse(
                savedBooking.getId(),
                savedBooking.getStatus(),
                savedBooking.getTotalAmount()
        );
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

    private double calculateTotal(List<ShowSeat> seats) {
        return seats.stream()
                .mapToDouble(ShowSeat::getPrice)
                .sum();
    }

    private void markSeatsBooked(List<ShowSeat> seats) {
        for (ShowSeat seat : seats) {
            seat.setStatus(SeatStatus.BOOKED);
        }
    }

    private Booking createBookingEntity(BookingRequest request, Show show, int seatCount, double totalAmount) {
        Booking booking = new Booking();
        booking.setUserId(request.getUserId());
        booking.setShow(show);
        booking.setNumberOfSeats(seatCount);
        booking.setStatus(BookingStatus.CONFIRMED);
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