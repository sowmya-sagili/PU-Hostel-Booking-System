package com.parul.hostel.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HostelDto {
    private Long id;
    private String name;
    private Integer fee;
    private Integer occupancy;
    private String facility;
    private String washroom;
    private String gender;
}
