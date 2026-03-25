package com.xyz.movie_booking.service;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PaymentWorker {

    private final BookingRepository bookingRepository;
    private final ShowSeatRepository showSeatRepository;
    private final BookingSeatRepository bookingSeatRepository;
    private final ShowRepository showRepository;

    public PaymentWorker(BookingRepository bookingRepository,
                         ShowSeatRepository showSeatRepository,
                         BookingSeatRepository bookingSeatRepository,
                         ShowRepository showRepository) {
        this.bookingRepository = bookingRepository;
        this.showSeatRepository = showSeatRepository;
        this.bookingSeatRepository = bookingSeatRepository;
        this.showRepository = showRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processPaymentTransactional(Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId).orElseThrow();

        if (booking.getStatus() != BookingStatus.PENDING) {
            return;
        }

        Long showId = booking.getShow().getId();

        List<BookingSeat> bookingSeats =
                bookingSeatRepository.findByBookingId(bookingId);

        List<String> seatNumbers = bookingSeats.stream()
                .map(BookingSeat::getSeatNumber)
                .toList();

        List<ShowSeat> seats =
                showSeatRepository.findSeatsForUpdate(showId, seatNumbers);

        boolean success = Math.random() > 0.5;

        if (success) {
            booking.setStatus(BookingStatus.CONFIRMED);
            seats.forEach(s -> s.setStatus(SeatStatus.BOOKED));

            Show show = showRepository.findById(showId).orElseThrow();
            show.setAvailableSeats(show.getAvailableSeats() - seats.size());

        } else {
            booking.setStatus(BookingStatus.FAILED);
            seats.forEach(s -> s.setStatus(SeatStatus.AVAILABLE));
        }
    }
}