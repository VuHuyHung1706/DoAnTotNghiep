package com.web.cinemaservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CinemaRequest {

    @NotBlank(message = "CINEMA_NAME_NOT_BLANK")
    private String name;

    @NotBlank(message = "ADDRESS_NOT_BLANK")
    private String address;

    private String phone;
}
