package com.hotel.payment_service.dto;

import com.hotel.payment_service.entity.Payment.PaymentMethod;
import jakarta.validation.constraints.NotNull;

import lombok.*;
import java.math.BigDecimal;

/**
 * PaymentRequest DTO - Data received FROM client to process a payment
 *
 * Example JSON:
 * {
 *   "bookingId": 1,
 *   "userId": 1,
 *   "amount": 5000.00,
 *   "paymentMethod": "UPI"
 * }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequest {

    @NotNull(message = "Booking ID is required")
    private Long bookingId;

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Amount is required")
    private BigDecimal amount;

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;
}
