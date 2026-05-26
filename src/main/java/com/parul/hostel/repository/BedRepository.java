package com.parul.hostel.repository;

import com.parul.hostel.entity.Bed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BedRepository extends JpaRepository<Bed, Long> {
    List<Bed> findByRoomIdAndIsBooked(Long roomId, Boolean isBooked);
}
