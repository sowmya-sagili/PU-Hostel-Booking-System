package com.parul.hostel.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "beds")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bed {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(name = "bed_number", nullable = false, length = 10)
    private String bedNumber;

    @Builder.Default
    @Column(name = "is_booked", nullable = false)
    private Boolean isBooked = false;
}
