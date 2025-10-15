package com.web.userservice.controller;

import com.web.userservice.dto.response.ApiResponse;
import com.web.userservice.dto.response.CustomerResponse;
import com.web.userservice.dto.response.ManagerResponse;
import com.web.userservice.dto.resquest.ChangePasswordRequest;
import com.web.userservice.dto.resquest.UpdateProfileRequest;
import com.web.userservice.dto.resquest.UserRegistrationRequest;
import com.web.userservice.service.account.AccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PostMapping("/register")
    public ApiResponse<CustomerResponse> registerUser(@Valid @RequestBody UserRegistrationRequest request) {
        return ApiResponse.<CustomerResponse>builder()
                .result(accountService.registerUser(request))
                .build();
    }

    @PostMapping("/send-otp")
    public ApiResponse<String> sendOtp(@Valid @RequestBody UserRegistrationRequest request) {
        accountService.sendOtp(request);
        return ApiResponse.<String>builder()
                .result("Please check your email for the OTP.")
                .build();
    }

    @PostMapping("/complete-register")
    public ApiResponse<CustomerResponse> completeRegister(@Valid @RequestBody UserRegistrationRequest request, @RequestParam String otp) {
        return ApiResponse.<CustomerResponse>builder()
                .result(accountService.completeRegistration(request, otp))
                .build();
    }

    @PostMapping("/change-password")
    public ApiResponse<String> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        accountService.changePassword(request);
        return ApiResponse.<String>builder()
                .result("Password changed successfully")
                .build();
    }

    @PutMapping("/profile")
    public ApiResponse<CustomerResponse> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        return ApiResponse.<CustomerResponse>builder()
                .result(accountService.updateProfile(request))
                .build();
    }

    @GetMapping("customer/profile")
    public ApiResponse<CustomerResponse> getMyProfile() {
        return ApiResponse.<CustomerResponse>builder()
                .result(accountService.getMyProfile())
                .build();
    }

    @GetMapping("manager/profile")
    public ApiResponse<ManagerResponse> getManagerProfile() {
        return ApiResponse.<ManagerResponse>builder()
                .result(accountService.getManager())
                .build();
    }

    @GetMapping("customer/{username}")
    public ApiResponse<CustomerResponse> getCustomerByUserName(@PathVariable String username) {
        return ApiResponse.<CustomerResponse>builder()
                .result(accountService.getCustomerByUsername(username))
                .build();
    }

    @GetMapping("customer/{id}")
    public ApiResponse<CustomerResponse> getCustomerById(@PathVariable Integer id) {
        return ApiResponse.<CustomerResponse>builder()
                .result(accountService.getCustomerById(id))
                .build();
    }

    @PostMapping("reset-password/customer/{username}")
    public ApiResponse<String> resetPassword(@PathVariable String username) {
        accountService.resetPassword(username);
        return ApiResponse.<String>builder()
                .result("Reset password success")
                .build();
    }
}
