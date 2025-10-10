package com.web.userservice.dto.response;

import com.web.userservice.enums.Position;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ManagerResponse {
    private Integer id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private Boolean gender;
    private Position position;
    private LocalDate dateOfBirth;
    private String address;
    private String idCard;
    private Boolean status;
}
