package com.parul.hostel.repository;

import com.parul.hostel.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    @Query("SELECT r FROM Room r JOIN r.floor f JOIN f.hostel h WHERE h.name = :hostelName")
    List<Room> findByHostelName(@Param("hostelName") String hostelName);
}
