package com.parul.hostel.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentProofResponseDto {
    private boolean success;
    private Long payment_id;
    private String payment_file;
    private String error;
}
