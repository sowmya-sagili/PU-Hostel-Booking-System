package com.parul.hostel.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "hostels")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Hostel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100)
    private String name;

    private Integer occupancy;

    private Integer fee;

    @Column(length = 10)
    private String gender;

    @Column(length = 100)
    private String facility;

    @Column(length = 100)
    private String washroom;
}
