package com.parul.hostel.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookingResponseDto {
    private boolean success;
    private Long student_id;
    private Long booking_id;
    private Long payment_id;
    private String error;
}
