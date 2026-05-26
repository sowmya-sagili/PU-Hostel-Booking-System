package com.parul.hostel.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bed_id", nullable = false)
    private Bed bed;

    @CreationTimestamp
    @Column(name = "booked_on", nullable = false, updatable = false)
    private LocalDateTime bookedOn;

    @Column(nullable = false)
    private Integer amount;

    @Builder.Default
    @Column(length = 20, nullable = false)
    private BookingStatus status = BookingStatus.PENDING; // PENDING / APPROVED / CANCELLED
}
