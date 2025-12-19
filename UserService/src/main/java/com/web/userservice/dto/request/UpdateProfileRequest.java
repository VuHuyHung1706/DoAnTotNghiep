package com.web.userservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProfileRequest {

    @NotBlank(message = "FIRST_NAME_NOT_BLANK")
    private String firstName;

    @NotBlank(message = "LAST_NAME_NOT_BLANK")
    private String lastName;

    private String email;
    private String phone;
    private Boolean gender;
    private LocalDate dateOfBirth;
    private String address;
}
