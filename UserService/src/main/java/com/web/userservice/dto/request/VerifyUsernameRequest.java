package com.web.userservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerifyUsernameRequest {
    @NotBlank(message = "USERNAME_NOT_BLANK")
    private String username;
}
