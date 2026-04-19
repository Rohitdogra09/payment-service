package com.hotel.payment_service.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * BookingResponse DTO - mirrors response from booking-service
 * Used by Feign client to get booking details
 * Fields match what booking-service returns
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingResponse {

    private Long id;
    private String bookingReference;
    private Long userId;
    private String userName;
    private String userEmail;
    private Long roomId;
    private String roomNumber;
    private Long hotelId;
    private String hotelName;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer numberOfGuests;
    private BigDecimal totalAmount;     // Amount to be paid
    private String status;
}
