package com.xyz.movie_booking.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    private final PaymentWorker worker;

    public PaymentService(PaymentWorker worker) {
        this.worker = worker;
    }

    @Async
    public void processPayment(Long bookingId) {
        worker.processPaymentTransactional(bookingId);
    }
}