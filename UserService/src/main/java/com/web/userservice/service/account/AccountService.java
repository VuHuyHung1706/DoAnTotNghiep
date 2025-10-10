package com.web.userservice.service.account;


import com.web.userservice.dto.response.CustomerResponse;
import com.web.userservice.dto.response.ManagerResponse;
import com.web.userservice.dto.resquest.ChangePasswordRequest;
import com.web.userservice.dto.resquest.UpdateProfileRequest;
import com.web.userservice.dto.resquest.UserRegistrationRequest;

public interface AccountService {
    CustomerResponse registerUser(UserRegistrationRequest request);
    void sendOtp(UserRegistrationRequest request);
    CustomerResponse completeRegistration(UserRegistrationRequest request, String otp);
    void changePassword(ChangePasswordRequest request);
    CustomerResponse updateProfile(UpdateProfileRequest request);
    CustomerResponse getMyProfile();
    ManagerResponse getManager();
    CustomerResponse getCustomerByUsername(String username);

}
