package com.xyz.movie_booking.service;

import com.xyz.movie_booking.dto.BookingRequest;
import com.xyz.movie_booking.dto.BookingResponse;
import com.xyz.movie_booking.exception.ResourceNotFoundException;
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
import jakarta.persistence.PessimisticLockException;
import jakarta.transaction.Transactional;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BookingService {

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
        List<String> requestedSeats = request.getSeatNumbers();

        requestedSeats.sort(String::compareTo);

        Show show = showRepository.findById(showId)
                .orElseThrow(() -> new ResourceNotFoundException("Show not found"));

        List<ShowSeat> seats;
        try {
            seats = showSeatRepository.findSeatsForUpdate(showId, requestedSeats);
        } catch (Exception ex) {
            throw new IllegalStateException("Seats are temporarily locked. Please retry.");
        }

        if (seats.size() != requestedSeats.size()) {
            throw new IllegalArgumentException("Some seats are invalid");
        }

        for (ShowSeat seat : seats) {
            if (seat.getStatus() != SeatStatus.AVAILABLE) {
                throw new IllegalArgumentException(
                        "Seat already booked: " + seat.getSeatNumber()
                );
            }
        }

        // Calculate price
        double totalAmount = seats.stream()
                .mapToDouble(ShowSeat::getPrice)
                .sum();

        // Mark seats
        for (ShowSeat seat : seats) {
            seat.setStatus(SeatStatus.BOOKED);
        }

        // Show is a managed entity (fetched within transaction), so this update
        // will be automatically persisted via JPA dirty checking
        show.setAvailableSeats(show.getAvailableSeats() - seats.size());

        Booking booking = new Booking();
        booking.setUserId(request.getUserId());
        booking.setShow(show);
        booking.setNumberOfSeats(seats.size());
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setTotalAmount(totalAmount);

        Booking savedBooking = bookingRepository.save(booking);

        List<BookingSeat> bookingSeats = seats.stream().map(seat -> {
            BookingSeat bs = new BookingSeat();
            bs.setBooking(savedBooking);
            bs.setSeatNumber(seat.getSeatNumber());
            return bs;
        }).toList();

        bookingSeatRepository.saveAll(bookingSeats);

        return new BookingResponse(
                savedBooking.getId(),
                savedBooking.getStatus(),
                savedBooking.getTotalAmount()
        );
    }
}
