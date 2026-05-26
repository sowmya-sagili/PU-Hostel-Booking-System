package com.parul.hostel.dto;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HostelsResponse {
    private List<HostelDto> hostels;
}
