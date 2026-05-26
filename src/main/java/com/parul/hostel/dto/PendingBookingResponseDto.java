package com.parul.hostel.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PendingBookingResponseDto {
    private Long id;
    private String student;
    private String email;
    private String hostel;
    private String room;
    private String bed;
    private String status;
    private String payment_file;
}
