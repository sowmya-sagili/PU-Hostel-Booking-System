package com.parul.hostel.service;

import com.parul.hostel.dto.*;
import com.parul.hostel.entity.*;
import com.parul.hostel.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {

    private final HostelRepository hostelRepository;
    private final FloorRepository floorRepository;
    private final RoomRepository roomRepository;
    private final BedRepository bedRepository;
    private final StudentRepository studentRepository;
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;

    private final PdfService pdfService;
    private final EmailService emailService;

    public HostelsResponse getAllHostels() {
        log.info("Fetching all hostels");
        List<HostelDto> list = hostelRepository.findAll().stream()
                .map(h -> HostelDto.builder()
                        .id(h.getId())
                        .name(h.getName())
                        .fee(h.getFee())
                        .occupancy(h.getOccupancy())
                        .facility(h.getFacility())
                        .washroom(h.getWashroom())
                        .gender(h.getGender())
                        .build())
                .collect(Collectors.toList());
        return new HostelsResponse(list);
    }

    public List<BedDto> getAvailableBeds(String hostelName, String floorRaw, String roomRaw) {
        log.info("Querying available beds for hostel: {}, floor: {}, room: {}", hostelName, floorRaw, roomRaw);
        
        List<Room> rooms = roomRepository.findByHostelName(hostelName);
        if (rooms.isEmpty()) {
            log.warn("No rooms found for hostel: {}", hostelName);
            return new ArrayList<>();
        }

        Room matchedRoom = resolveRoom(rooms, floorRaw, roomRaw);
        if (matchedRoom == null) {
            log.warn("No matching room found for floor: {}, room: {}", floorRaw, roomRaw);
            return new ArrayList<>();
        }

        log.info("Resolved room ID: {} for number: {}", matchedRoom.getId(), matchedRoom.getRoomNumber());
        List<Bed> beds = bedRepository.findByRoomIdAndIsBooked(matchedRoom.getId(), false);
        
        return beds.stream()
                .map(b -> new BedDto(b.getId(), b.getBedNumber()))
                .collect(Collectors.toList());
    }

    private Room resolveRoom(List<Room> rooms, String floorRaw, String roomRaw) {
        Integer floorInt = null;
        Integer roomInt = null;
        try { floorInt = Integer.parseInt(floorRaw.trim()); } catch (Exception e) {}
        try { roomInt = Integer.parseInt(roomRaw.trim()); } catch (Exception e) {}

        // 1) Strict match
        for (Room r : rooms) {
            if (r.getFloor().getFloorNo().toString().equalsIgnoreCase(floorRaw) 
                    && r.getRoomNumber().equalsIgnoreCase(roomRaw)) {
                return r;
            }
        }

        // 2) Numeric match
        if (floorInt != null || roomInt != null) {
            for (Room r : rooms) {
                boolean floorMatch = floorInt != null 
                        ? r.getFloor().getFloorNo().equals(floorInt) 
                        : r.getFloor().getFloorNo().toString().equalsIgnoreCase(floorRaw);
                boolean roomMatch = roomInt != null 
                        ? r.getRoomNumber().equalsIgnoreCase(String.valueOf(roomInt)) 
                        : r.getRoomNumber().equalsIgnoreCase(roomRaw);
                if (floorMatch && roomMatch) {
                    return r;
                }
            }
        }

        // 3) Candidate match (prefixes "Floor " and "Room ")
        List<String> floorCandidates = new ArrayList<>();
        floorCandidates.add(floorRaw);
        floorCandidates.add("Floor " + floorRaw);
        if (floorInt != null) {
            floorCandidates.add(String.valueOf(floorInt));
            floorCandidates.add("Floor " + floorInt);
        }

        List<String> roomCandidates = new ArrayList<>();
        roomCandidates.add(roomRaw);
        roomCandidates.add("Room " + roomRaw);
        if (roomInt != null) {
            roomCandidates.add(String.valueOf(roomInt));
            roomCandidates.add("Room " + roomInt);
        }

        for (Room r : rooms) {
            String rf = String.valueOf(r.getFloor().getFloorNo());
            String rr = r.getRoomNumber();
            for (String fc : floorCandidates) {
                for (String rc : roomCandidates) {
                    if (rf.equalsIgnoreCase(fc) && rr.equalsIgnoreCase(rc)) {
                        return r;
                    }
                }
            }
        }

        // 4) Partial match (contains substring)
        String substring = roomInt != null ? String.valueOf(roomInt) : roomRaw;
        for (Room r : rooms) {
            if (r.getRoomNumber().toLowerCase().contains(substring.toLowerCase())) {
                return r;
            }
        }

        return null;
    }

    @Transactional
    public BookingResponseDto bookRoom(BookingRequestDto req) {
        log.info("Processing booking request for student email: {}, bed ID: {}", req.getStudent_email(), req.getBed_id());

        // Validate if student email has an active booking
        Optional<Booking> activeBooking = bookingRepository.findByStudentEmail(req.getStudent_email());
        if (activeBooking.isPresent() && activeBooking.get().getStatus() != BookingStatus.CANCELLED) {
            log.warn("Booking rejected: Student with email {} already has an active booking ID: {}", 
                    req.getStudent_email(), activeBooking.get().getId());
            throw new IllegalArgumentException("This email address has already been used to book a room.");
        }

        // Validate bed availability
        Bed bed = bedRepository.findById(req.getBed_id())
                .orElseThrow(() -> new IllegalArgumentException("Invalid bed selected"));
        
        if (bed.getIsBooked()) {
            log.warn("Booking rejected: Bed ID {} is already booked.", req.getBed_id());
            throw new IllegalArgumentException("Sorry, this bed was just booked.");
        }

        // Check if student exists or create new
        Student student = studentRepository.findByEmail(req.getStudent_email()).orElse(null);
        if (student == null) {
            log.info("Creating new student record for: {}", req.getStudent_email());
            student = Student.builder()
                    .name(req.getStudent_name())
                    .email(req.getStudent_email())
                    .phone(req.getStudent_phone())
                    .gender(req.getStudent_gender())
                    .password("hashed_placeholder")
                    .build();
            student = studentRepository.save(student);
        } else {
            student.setPhone(req.getStudent_phone());
            student.setGender(req.getStudent_gender());
            student = studentRepository.save(student);
            log.info("Updated existing student phone and gender for booking: {}", student.getEmail());
        }

        // Lock bed
        bed.setIsBooked(true);
        bedRepository.save(bed);

        // Create booking
        Booking booking = Booking.builder()
                .student(student)
                .bed(bed)
                .amount(req.getAmount())
                .status(BookingStatus.PENDING)
                .build();
        booking = bookingRepository.save(booking);

        // Create payment
        Payment payment = Payment.builder()
                .booking(booking)
                .amount(req.getAmount())
                .status("Pending")
                .build();
        payment = paymentRepository.save(payment);

        log.info("Booking created successfully. Booking ID: {}, Student ID: {}, Payment ID: {}", 
                booking.getId(), student.getId(), payment.getId());

        return BookingResponseDto.builder()
                .success(true)
                .student_id(student.getId())
                .booking_id(booking.getId())
                .payment_id(payment.getId())
                .build();
    }

    public List<PendingBookingResponseDto> getPendingBookings() {
        log.info("Retrieving all pending bookings");
        List<Booking> bookings = bookingRepository.findByStatus(BookingStatus.PENDING);
        
        List<PendingBookingResponseDto> list = new ArrayList<>();
        for (Booking b : bookings) {
            Optional<Payment> paymentOpt = paymentRepository.findByBookingId(b.getId());
            String paymentFile = null;
            if (paymentOpt.isPresent() && paymentOpt.get().getPaymentFile() != null) {
                paymentFile = "/uploads/" + paymentOpt.get().getPaymentFile();
            }
            
            Hostel hostel = b.getBed().getRoom().getFloor().getHostel();
            Room room = b.getBed().getRoom();
            
            list.add(PendingBookingResponseDto.builder()
                    .id(b.getId())
                    .student(b.getStudent().getName())
                    .email(b.getStudent().getEmail())
                    .hostel(hostel.getName())
                    .room(room.getRoomNumber())
                    .bed(b.getBed().getBedNumber())
                    .status(b.getStatus().getValue())
                    .payment_file(paymentFile)
                    .build());
        }
        return list;
    }

    @Transactional
    public void approveBooking(Long bookingId) {
        log.info("Approving booking ID: {}", bookingId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + bookingId));
        
        booking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking);

        // Update payment status if present
        paymentRepository.findByBookingId(bookingId).ifPresent(p -> {
            p.setStatus("Verified");
            paymentRepository.save(p);
        });

        // Generate PDF receipt
        pdfService.generateBookingPdf(booking);

        // Send confirmation email
        emailService.sendBookingEmail(booking.getStudent().getEmail(), booking, "approved");
        
        log.info("Booking ID: {} approved successfully.", bookingId);
    }

    @Transactional
    public void cancelBooking(Long bookingId) {
        log.info("Cancelling booking ID: {}", bookingId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + bookingId));
        
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        // Release the bed lock
        Bed bed = booking.getBed();
        bed.setIsBooked(false);
        bedRepository.save(bed);

        // Update payment status if present
        paymentRepository.findByBookingId(bookingId).ifPresent(p -> {
            p.setStatus("Rejected");
            paymentRepository.save(p);
        });

        // Send cancellation email
        emailService.sendBookingEmail(booking.getStudent().getEmail(), booking, "cancelled");

        log.info("Booking ID: {} cancelled successfully.", bookingId);
    }

    @Transactional
    public PaymentProofResponseDto uploadPaymentProof(Long paymentId, String filename) {
        log.info("Updating payment ID: {} with proof filename: {}", paymentId, filename);
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid payment ID"));

        payment.setPaymentFile(filename);
        payment.setStatus("Uploaded");
        paymentRepository.save(payment);

        return PaymentProofResponseDto.builder()
                .success(true)
                .payment_id(payment.getId())
                .payment_file(filename)
                .build();
    }

    public byte[] getBookingReceiptPdfBytes(Long bookingId, String loggedInEmail, boolean isAdmin) {
        log.info("Request to get booking receipt PDF bytes for booking ID: {}, user: {}, isAdmin: {}", 
                bookingId, loggedInEmail, isAdmin);
        
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + bookingId));
        
        if (!isAdmin && !booking.getStudent().getEmail().equalsIgnoreCase(loggedInEmail)) {
            log.warn("Access denied: User {} tried to access booking receipt for booking ID: {}", 
                    loggedInEmail, bookingId);
            throw new org.springframework.security.access.AccessDeniedException("You do not have permission to access this receipt.");
        }
        
        Optional<Payment> paymentOpt = paymentRepository.findByBookingId(bookingId);
        String paymentFile = paymentOpt.map(Payment::getPaymentFile).orElse("N/A");
        
        return pdfService.generateBookingReceiptPdfBytes(booking, paymentFile);
    }
}
