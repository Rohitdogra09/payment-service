package com.hotel.payment_service.dto;
import com.hotel.payment_service.entity.Payment.PaymentMethod;
import com.hotel.payment_service.entity.Payment.PaymentStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * PaymentResponse DTO - Data sent BACK to client after payment
 *
 * Example JSON response:
 * {
 *   "id": 1,
 *   "transactionId": "TXN-AB12CD34",
 *   "bookingId": 1,
 *   "amount": 5000.00,
 *   "paymentMethod": "UPI",
 *   "status": "SUCCESS",
 *   "createdAt": "2024-01-15T10:30:00"
 * }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {

    private Long id;
    private String transactionId;       // Unique transaction reference
    private Long bookingId;
    private Long userId;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private PaymentStatus status;
    private String failureReason;       // Populated only if payment failed
    private LocalDateTime createdAt;
}
