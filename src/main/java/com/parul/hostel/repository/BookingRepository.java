package com.parul.hostel.repository;

import com.parul.hostel.entity.Booking;
import com.parul.hostel.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByStatus(BookingStatus status);
    Optional<Booking> findByStudentEmail(String email);
    Optional<Booking> findByStudentId(Long studentId);
}
