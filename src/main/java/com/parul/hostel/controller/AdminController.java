package com.parul.hostel.controller;

import com.parul.hostel.dto.ApiResponse;
import com.parul.hostel.dto.PendingBookingResponseDto;
import com.parul.hostel.service.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final BookingService bookingService;

    @GetMapping("/pending_bookings")
    public ResponseEntity<List<PendingBookingResponseDto>> pendingBookings() {
        log.info("REST request to list all pending bookings for admin");
        List<PendingBookingResponseDto> bookings = bookingService.getPendingBookings();
        return ResponseEntity.ok(bookings);
    }

    @PostMapping("/approve_booking/{bookingId}")
    public ResponseEntity<ApiResponse> approveBooking(@PathVariable("bookingId") Long bookingId) {
        log.info("REST request to approve booking ID: {}", bookingId);
        try {
            bookingService.approveBooking(bookingId);
            return ResponseEntity.ok(new ApiResponse(true, null));
        } catch (IllegalArgumentException e) {
            log.warn("Validation error while approving booking ID: {}", bookingId, e);
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage()));
        } catch (Exception e) {
            log.error("Internal error while approving booking ID: {}", bookingId, e);
            return ResponseEntity.internalServerError().body(new ApiResponse(false, "Internal server error: " + e.getMessage()));
        }
    }

    @PostMapping("/cancel_booking/{bookingId}")
    public ResponseEntity<ApiResponse> cancelBooking(@PathVariable("bookingId") Long bookingId) {
        log.info("REST request to cancel booking ID: {}", bookingId);
        try {
            bookingService.cancelBooking(bookingId);
            return ResponseEntity.ok(new ApiResponse(true, null));
        } catch (IllegalArgumentException e) {
            log.warn("Validation error while cancelling booking ID: {}", bookingId, e);
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage()));
        } catch (Exception e) {
            log.error("Internal error while cancelling booking ID: {}", bookingId, e);
            return ResponseEntity.internalServerError().body(new ApiResponse(false, "Internal server error: " + e.getMessage()));
        }
    }
}
