# 🎬 Movie Booking System

A scalable backend system for movie ticket booking with support for seat locking, async payment, dynamic pricing, and extensible discount rules.

---

# 🚀 Features

- Search shows by movie, city, date
- View seat availability with dynamic pricing
- Book seats with concurrency control
- Async payment processing with polling
- Extensible pricing and discount system
- Robust validation and exception handling

---

# 📡 APIs

## 🟢 Reader APIs (3)

### 1. Get Shows
GET /shows?movieId=&city=&date=

Fetch available shows for a movie in a city on a given date.

Response includes:
- Movie details
- Theatres and show timings
- Available seats

---

### 2. Get Seats for Show
GET /shows/{showId}/seats

Returns all seats for a show.

Response includes:
- Seat number
- Status (AVAILABLE / LOCKED / BOOKED)
- Price (after pricing strategy)

---

### 3. Get Booking (Polling API)
GET /bookings/{bookingId}

Fetch booking status after async payment.

Response includes:
- Booking ID
- Status (PENDING / CONFIRMED / FAILED)
- Seats
- Total amount
- Polling URL

---

## 🔴 Writer API (1)

### 4. Create Booking
POST /bookings

Request:
{
"userId": 1,
"showId": 10,
"seatNumbers": ["A1", "A2"]
}

Flow:
1. Lock seats (pessimistic locking)
2. Validate availability
3. Apply pricing strategy
4. Apply discount chain
5. Create booking (PENDING)
6. Trigger async payment

Response:
- Booking ID
- Status = PENDING
- Polling URL

---

# 🧱 Data Modeling

Core Entities:
- Movie
- Theatre
- Show
- ShowSeat
- Booking
- BookingSeat

Relationships:
- Movie → Show (1:N)
- Theatre → Show (1:N)
- Show → ShowSeat (1:N)
- Booking → BookingSeat (1:N)

---

# 📌 Indexing

- shows(movie_id, show_date)
- shows(theatre_id)
- show_seat(show_id)
- booking(user_id)

---

# 🔐 Unique Constraints

- show_seat(show_id, seat_number) → prevents double booking
- booking_seat(booking_id, seat_number) → avoids duplicate seats

Note:
No unique constraint on (user_id, show_id) since users can book multiple times.

---

# ⚙️ Concurrency & Transactions

Seat Locking:
- Uses pessimistic locking (FOR UPDATE)
- Prevents concurrent booking of same seat

Seat lifecycle:
AVAILABLE → LOCKED → BOOKED / AVAILABLE

Transaction:
- Booking runs in transaction
- Payment triggered after commit

---

# ⚡ Async Payment Flow

Booking (PENDING)
↓
Async Payment
↓
CONFIRMED / FAILED

Behavior:
- Success → seats marked BOOKED
- Failure → seats released (AVAILABLE)

Mock:
- Simulated delay (~2 seconds)
- Random failure (~10%)

---

# 🧠 Design Patterns

## Pricing Strategy (Strategy Pattern)

- Supports dynamic pricing:
    - Base pricing
    - Surge pricing
- Applied in:
    - Seat API (per seat)
    - Booking API (total)

---

## Discount Chain (Chain of Responsibility)

- Extensible discount rules:
    - Bulk discount
- Chain example:
 Bulk → NoDiscount

- Config-driven chaining
- Uses @Primary bean for injection

---

# 🧩 Validation & Exception Handling

Validation:
- @Valid on request DTOs
- NotNull, NotEmpty checks

Global Exception Handling:
- Type mismatch
- Validation errors
- Resource not found
- Lock failures
- Generic exceptions

---

# 🗄️ Repository Layer

- Spring Data JPA
- Minimal custom queries
- Custom queries:
    - Show search
    - Seat locking (FOR UPDATE)

---

# 📊 Logging & Extensibility

Logging:
- Request logs
- Error logs
- Warning logs

Extensible for:
- Metrics (Datadog)
- Notifications (email/SMS)
- Audit logging

---

# ⚖️ Trade-offs

- Pessimistic locking → strong consistency
- Async payment → better UX
- Polling → simple integration
- Enum stored as STRING → flexible schema
- Strategy pattern → flexible pricing
- Chain pattern → extensible discounts

---

# 🧘 Summary

This system demonstrates:
- Clean architecture
- Concurrency handling
- Async workflow
- Extensible business logic
- Real-world booking design

---

# 🚀 Future Enhancements

- Redis caching (seat availability, show search)
- Kafka for async event processing (booking, payment, notifications)
- Webhooks instead of polling for booking status updates
- Rate limiting to prevent abuse
- Idempotency keys
- Payment integration (real payment gateway like Stripe/Razorpay)
- Notification system (email/SMS on booking confirmation/failure)
- Ranking / sorting (popular shows, trending movies, fast-filling shows)
- Redis-based distributed locking (instead of DB pessimistic locks for better scalability)

