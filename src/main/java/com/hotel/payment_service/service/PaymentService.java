package com.hotel.payment_service.service;
import com.hotel.payment_service.dto.BookingResponse;
import com.hotel.payment_service.dto.PaymentRequest;
import com.hotel.payment_service.dto.PaymentResponse;
import com.hotel.payment_service.entity.Payment;
import com.hotel.payment_service.feign.BookingServiceClient;
import com.hotel.payment_service.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * PaymentService - Core business logic for payment processing
 *
 * Flow:
 * 1. Validate booking exists and is PENDING
 * 2. Validate payment amount matches booking amount
 * 3. Simulate payment processing (random success/fail)
 * 4. If SUCCESS → confirm booking via Feign call
 * 5. Save and return payment result
 *
 * NOTE: This simulates a real payment gateway
 * In production, integrate with Razorpay, Stripe, PayU etc.
 */
@Service
@RequiredArgsConstructor
@Slf4j      // Lombok: generates log object for logging
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingServiceClient bookingServiceClient;    // Feign → booking-service

    /**
     * PROCESS a payment for a booking
     */
    public PaymentResponse processPayment(PaymentRequest request) {

        // Step 1: Fetch booking details from booking-service
        log.info("Fetching booking details for bookingId: {}", request.getBookingId());
        BookingResponse booking = bookingServiceClient.getBookingById(request.getBookingId());

        // Step 2: Validate booking is in PENDING status
        if (!"PENDING".equals(booking.getStatus())) {
            throw new RuntimeException(
                    "Payment can only be made for PENDING bookings. " +
                            "Current status: " + booking.getStatus());
        }

        // Step 3: Validate payment amount matches booking amount
        if (request.getAmount().compareTo(booking.getTotalAmount()) != 0) {
            throw new RuntimeException(
                    "Payment amount " + request.getAmount() +
                            " does not match booking amount " + booking.getTotalAmount());
        }

        // Step 4: Generate unique transaction ID
        String transactionId = "TXN-" + UUID.randomUUID()
                .toString()
                .substring(0, 8)
                .toUpperCase();

        // Step 5: Simulate payment gateway processing
        // In production: call Razorpay/Stripe API here
        // Here we simulate 90% success rate
        boolean paymentSuccessful = simulatePaymentGateway();

        Payment payment;

        if (paymentSuccessful) {
            // Step 6a: Payment SUCCESS
            log.info("Payment successful for bookingId: {}", request.getBookingId());

            payment = Payment.builder()
                    .transactionId(transactionId)
                    .bookingId(request.getBookingId())
                    .userId(request.getUserId())
                    .amount(request.getAmount())
                    .paymentMethod(request.getPaymentMethod())
                    .status(Payment.PaymentStatus.SUCCESS)
                    .build();

            // Step 7: Confirm booking in booking-service via Feign
            // This changes booking status: PENDING → CONFIRMED
            log.info("Confirming booking: {}", request.getBookingId());
            bookingServiceClient.confirmBooking(request.getBookingId());

        } else {
            // Step 6b: Payment FAILED
            log.warn("Payment failed for bookingId: {}", request.getBookingId());

            payment = Payment.builder()
                    .transactionId(transactionId)
                    .bookingId(request.getBookingId())
                    .userId(request.getUserId())
                    .amount(request.getAmount())
                    .paymentMethod(request.getPaymentMethod())
                    .status(Payment.PaymentStatus.FAILED)
                    .failureReason("Payment declined by gateway")
                    .build();
        }

        // Step 8: Save payment record regardless of success/failure
        Payment saved = paymentRepository.save(payment);
        return mapToResponse(saved);
    }

    /**
     * GET payment by ID
     */
    public PaymentResponse getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + id));
        return mapToResponse(payment);
    }

    /**
     * GET payment by transaction ID
     */
    public PaymentResponse getPaymentByTransactionId(String transactionId) {
        Payment payment = paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new RuntimeException(
                        "Payment not found with transactionId: " + transactionId));
        return mapToResponse(payment);
    }

    /**
     * GET all payments for a specific booking
     */
    public List<PaymentResponse> getPaymentsByBooking(Long bookingId) {
        return paymentRepository.findByBookingId(bookingId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * GET all payments made by a specific user
     */
    public List<PaymentResponse> getPaymentsByUser(Long userId) {
        return paymentRepository.findByUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * REFUND a payment
     * Changes payment status: SUCCESS → REFUNDED
     */
    public PaymentResponse refundPayment(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + id));

        // Only successful payments can be refunded
        if (payment.getStatus() != Payment.PaymentStatus.SUCCESS) {
            throw new RuntimeException("Only SUCCESS payments can be refunded");
        }

        payment.setStatus(Payment.PaymentStatus.REFUNDED);
        log.info("Payment refunded for transactionId: {}", payment.getTransactionId());

        return mapToResponse(paymentRepository.save(payment));
    }

    /**
     * PRIVATE - Simulates a payment gateway (90% success rate)
     * Replace this with real gateway SDK in production
     * e.g. Razorpay, Stripe, PayU, CCAvenue
     */
    private boolean simulatePaymentGateway() {
        // Math.random() returns 0.0 to 1.0
        // Returns true 90% of the time
        return Math.random() > 0.1;
    }

    /**
     * PRIVATE HELPER - Maps Payment entity → PaymentResponse DTO
     */
    private PaymentResponse mapToResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .transactionId(payment.getTransactionId())
                .bookingId(payment.getBookingId())
                .userId(payment.getUserId())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod())
                .status(payment.getStatus())
                .failureReason(payment.getFailureReason())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}
