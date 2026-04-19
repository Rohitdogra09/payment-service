package com.hotel.payment_service.repository;
import com.hotel.payment_service.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * PaymentRepository - Database access for Payment entity
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // Find payment by unique transaction ID
    Optional<Payment> findByTransactionId(String transactionId);

    // Get all payments for a specific booking
    List<Payment> findByBookingId(Long bookingId);

    // Get all payments made by a specific user
    List<Payment> findByUserId(Long userId);

    // Get payments by status
    List<Payment> findByStatus(Payment.PaymentStatus status);
}
