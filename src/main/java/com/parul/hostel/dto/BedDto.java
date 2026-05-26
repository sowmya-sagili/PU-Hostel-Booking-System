package com.parul.hostel.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BedDto {
    private Long id;
    private String bed_number;
}
