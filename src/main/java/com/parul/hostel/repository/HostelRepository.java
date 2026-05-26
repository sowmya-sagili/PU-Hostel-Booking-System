package com.parul.hostel.repository;

import com.parul.hostel.entity.Hostel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HostelRepository extends JpaRepository<Hostel, Long> {
}
