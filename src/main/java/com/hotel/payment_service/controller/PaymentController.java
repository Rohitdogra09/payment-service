package com.hotel.payment_service.controller;
import com.hotel.payment_service.dto.PaymentRequest;
import com.hotel.payment_service.dto.PaymentResponse;
import com.hotel.payment_service.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * PaymentController - REST API endpoints for Payment operations
 *
 * Base URL: http://localhost:8084/api/payments
 *
 * POST   /api/payments                        → Process payment
 * GET    /api/payments/{id}                   → Get payment by ID
 * GET    /api/payments/transaction/{txnId}    → Get by transaction ID
 * GET    /api/payments/booking/{bookingId}    → Get payments for booking
 * GET    /api/payments/user/{userId}          → Get payments by user
 * PUT    /api/payments/{id}/refund            → Refund a payment
 */
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * POST /api/payments
     * Process a new payment for a booking
     * On success → booking is automatically confirmed!
     */
    @PostMapping
    public ResponseEntity<PaymentResponse> processPayment(
            @Valid @RequestBody PaymentRequest request) {
        return new ResponseEntity<>(
                paymentService.processPayment(request), HttpStatus.CREATED);
    }

    /**
     * GET /api/payments/{id}
     * Get payment details by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPaymentById(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.getPaymentById(id));
    }

    /**
     * GET /api/payments/transaction/TXN-AB12CD34
     * Get payment by transaction ID
     */
    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<PaymentResponse> getPaymentByTransactionId(
            @PathVariable String transactionId) {
        return ResponseEntity.ok(paymentService.getPaymentByTransactionId(transactionId));
    }

    /**
     * GET /api/payments/booking/1
     * Get all payments for a specific booking
     */
    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByBooking(
            @PathVariable Long bookingId) {
        return ResponseEntity.ok(paymentService.getPaymentsByBooking(bookingId));
    }

    /**
     * GET /api/payments/user/1
     * Get all payments made by a specific user
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByUser(
            @PathVariable Long userId) {
        return ResponseEntity.ok(paymentService.getPaymentsByUser(userId));
    }

    /**
     * PUT /api/payments/1/refund
     * Refund a successful payment
     */
    @PutMapping("/{id}/refund")
    public ResponseEntity<PaymentResponse> refundPayment(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.refundPayment(id));
    }
}
