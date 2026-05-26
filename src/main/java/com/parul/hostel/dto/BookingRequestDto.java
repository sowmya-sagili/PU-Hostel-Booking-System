package com.parul.hostel.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingRequestDto {

    @NotNull(message = "Bed ID is required")
    private Long bed_id;

    @NotBlank(message = "Student name is required")
    private String student_name;

    @NotBlank(message = "Student email is required")
    @Email(message = "Invalid email format")
    private String student_email;

    private String student_phone;
    private String student_gender;

    @NotNull(message = "Amount is required")
    private Integer amount;
}
