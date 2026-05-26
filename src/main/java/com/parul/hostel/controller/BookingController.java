package com.parul.hostel.controller;

import com.parul.hostel.dto.*;
import com.parul.hostel.service.BookingService;
import com.parul.hostel.service.UploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {

    private final BookingService bookingService;
    private final UploadService uploadService;

    @GetMapping("/hostels")
    public ResponseEntity<HostelsResponse> listHostels() {
        log.info("REST request to list all hostels");
        HostelsResponse response = bookingService.getAllHostels();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/available_beds")
    public ResponseEntity<BedsResponse> availableBeds(
            @RequestParam("hostel") String hostel,
            @RequestParam("floor") String floor,
            @RequestParam("room") String room) {
        log.info("REST request for available beds in hostel: {}, floor: {}, room: {}", hostel, floor, room);
        
        List<BedDto> beds = bookingService.getAvailableBeds(hostel, floor, room);
        BedsResponse response = BedsResponse.builder()
                .success(true)
                .beds(beds)
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/book")
    public ResponseEntity<BookingResponseDto> bookRoom(@Valid @RequestBody BookingRequestDto requestDto) {
        log.info("REST request to book room: {}", requestDto);
        BookingResponseDto response = bookingService.bookRoom(requestDto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/payment/proof")
    public ResponseEntity<PaymentProofResponseDto> uploadPaymentProof(
            @RequestParam("payment_id") Long paymentId,
            @RequestParam("file") MultipartFile file) {
        log.info("REST request to upload payment proof for payment ID: {}", paymentId);
        
        if (file.isEmpty()) {
            log.warn("Uploaded file is empty for payment ID: {}", paymentId);
            return ResponseEntity.badRequest().body(PaymentProofResponseDto.builder()
                    .success(false)
                    .error("Uploaded file is empty.")
                    .build());
        }

        try {
            String prefix = "payment_" + paymentId;
            String savedFilename = uploadService.saveFile(file, prefix);
            
            PaymentProofResponseDto response = bookingService.uploadPaymentProof(paymentId, savedFilename);
            return ResponseEntity.ok(response);
            
        } catch (IOException e) {
            log.error("IO error while saving payment proof file for payment ID: {}", paymentId, e);
            return ResponseEntity.internalServerError().body(PaymentProofResponseDto.builder()
                    .success(false)
                    .error("Server storage error: Could not save proof file.")
                    .build());
        } catch (IllegalArgumentException e) {
            log.warn("Validation error during payment upload: {}", e.getMessage());
            return ResponseEntity.badRequest().body(PaymentProofResponseDto.builder()
                    .success(false)
                    .error(e.getMessage())
                    .build());
        }
    }

    @GetMapping("/booking/receipt/{bookingId}")
    public ResponseEntity<byte[]> getBookingReceipt(
            @PathVariable("bookingId") Long bookingId,
            org.springframework.security.core.Authentication authentication) {
        
        String loggedInEmail = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        
        try {
            byte[] pdfBytes = bookingService.getBookingReceiptPdfBytes(bookingId, loggedInEmail, isAdmin);
            
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_PDF);
            headers.setContentDisposition(org.springframework.http.ContentDisposition.attachment()
                    .filename("booking_receipt_" + bookingId + ".pdf")
                    .build());
            
            return ResponseEntity.ok().headers(headers).body(pdfBytes);
        } catch (org.springframework.security.access.AccessDeniedException e) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.FORBIDDEN).body(null);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            log.error("Error generating booking receipt PDF for booking ID: {}", bookingId, e);
            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
