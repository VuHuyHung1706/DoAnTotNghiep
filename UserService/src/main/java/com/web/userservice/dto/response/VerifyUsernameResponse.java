package com.web.userservice.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerifyUsernameResponse {
    private String email;
    private String maskedEmail; // email ẩn bớt để hiển thị: ex***@gmail.com
}
