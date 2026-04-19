package com.hotel.payment_service.feign;

import com.hotel.payment_service.dto.BookingResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

/**
 * BookingServiceClient - Feign client to call booking-service
 *
 * Used to:
 * 1. Fetch booking details before processing payment
 * 2. Confirm the booking after successful payment
 */
@FeignClient(name = "booking-service")
public interface BookingServiceClient {

    /**
     * Calls: GET http://booking-service/api/bookings/{id}
     * Fetches booking details to validate before payment
     */
    @GetMapping("/api/bookings/{id}")
    BookingResponse getBookingById(@PathVariable Long id);

    /**
     * Calls: PUT http://booking-service/api/bookings/{id}/confirm
     * Confirms the booking after successful payment
     * This is the key integration between payment and booking!
     */
    @PutMapping("/api/bookings/{id}/confirm")
    BookingResponse confirmBooking(@PathVariable Long id);
}
