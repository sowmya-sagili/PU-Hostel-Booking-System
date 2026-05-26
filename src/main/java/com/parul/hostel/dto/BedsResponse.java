package com.parul.hostel.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BedsResponse {
    private boolean success;
    private List<BedDto> beds;
    private String error;
}
