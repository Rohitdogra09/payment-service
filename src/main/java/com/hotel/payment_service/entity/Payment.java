package com.hotel.payment_service.entity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Payment Entity - Maps to 'payments' table in MySQL
 *
 * Stores all payment transactions for hotel bookings
 * Each payment is linked to a booking via bookingId
 * We simulate payment processing here (no real gateway)
 */
@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String transactionId;       // Unique transaction ID e.g. "TXN-XXXXXXXX"

    @Column(nullable = false)
    private Long bookingId;             // Reference to booking-service booking

    @Column(nullable = false)
    private Long userId;                // Who made the payment

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;          // Payment amount

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;    // HOW they paid

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;           // Payment result

    private String failureReason;           // Reason if payment failed

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    /**
     * Available payment methods
     * CREDIT_CARD  → Visa, Mastercard etc.
     * DEBIT_CARD   → Bank debit card
     * UPI          → GPay, PhonePe, Paytm (popular in India)
     * NET_BANKING  → Online bank transfer
     * WALLET       → Digital wallet
     * CASH         → Pay at hotel
     */
    public enum PaymentMethod {
        CREDIT_CARD,
        DEBIT_CARD,
        UPI,
        NET_BANKING,
        WALLET,
        CASH
    }

    /**
     * Payment status lifecycle
     * PENDING   → Payment initiated but not processed
     * SUCCESS   → Payment successful
     * FAILED    → Payment failed
     * REFUNDED  → Payment refunded to customer
     */
    public enum PaymentStatus {
        PENDING,
        SUCCESS,
        FAILED,
        REFUNDED
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) status = PaymentStatus.PENDING;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
