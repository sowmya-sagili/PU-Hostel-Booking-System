package com.parul.hostel.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @Column(nullable = false)
    private Integer amount;

    @Column(name = "payment_file", length = 200)
    private String paymentFile;

    @Builder.Default
    @Column(length = 20, nullable = false)
    private String status = "Pending"; // Pending / Uploaded / Verified / Rejected

    @Column(name = "verified_by", length = 100)
    private String verifiedBy;

    @Column(name = "verified_on")
    private LocalDateTime verifiedOn;
}
