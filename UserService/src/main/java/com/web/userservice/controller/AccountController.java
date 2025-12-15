package com.web.userservice.controller;

import com.web.userservice.dto.request.StaffRegistrationRequest;
import com.web.userservice.dto.request.UpdateStaffRequest;
import com.web.userservice.dto.response.ApiResponse;
import com.web.userservice.dto.response.CustomerResponse;
import com.web.userservice.dto.response.ManagerResponse;
import com.web.userservice.dto.resquest.ChangePasswordRequest;
import com.web.userservice.dto.resquest.UpdateProfileRequest;
import com.web.userservice.dto.resquest.UserRegistrationRequest;
import com.web.userservice.service.account.AccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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

    @PostMapping("reset-password/customer/{username}")
    public ApiResponse<String> resetPassword(@PathVariable String username) {
        accountService.resetPassword(username);
        return ApiResponse.<String>builder()
                .result("Reset password success")
                .build();
    }

    @GetMapping("/customers")
    public ApiResponse<Page<CustomerResponse>> getAllCustomers(Pageable pageable) {
        return ApiResponse.<Page<CustomerResponse>>builder()
                .result(accountService.getAllCustomers(pageable))
                .build();
    }

    @GetMapping("/customers/search")
    public ApiResponse<Page<CustomerResponse>> searchCustomers(Pageable pageable, @RequestParam(required = false) String keyword) {
        return ApiResponse.<Page<CustomerResponse>>builder()
                .result(accountService.searchCustomers(keyword, pageable))
                .build();
    }

    @PostMapping("/customers")
    public ApiResponse<CustomerResponse> createCustomer(@Valid @RequestBody UserRegistrationRequest request) {
        return ApiResponse.<CustomerResponse>builder()
                .result(accountService.createCustomer(request))
                .build();
    }

    @DeleteMapping("/customers/{username}")
    public ApiResponse<String> deleteCustomer(@PathVariable String username) {
        accountService.deleteCustomer(username);
        return ApiResponse.<String>builder()
                .result("Customer soft deleted successfully")
                .build();
    }

    @PutMapping("/customers/{username}/password")
    public ApiResponse<String> updateCustomerPassword(
            @PathVariable String username,
            @RequestBody ChangePasswordRequest request) {
        accountService.updateCustomerPassword(username, request.getNewPassword());
        return ApiResponse.<String>builder()
                .result("Password updated successfully")
                .build();
    }

    @PutMapping("/customers/{username}")
    public ApiResponse<CustomerResponse> updateCustomer(
            @PathVariable String username,
            @Valid @RequestBody UpdateProfileRequest request) {
        return ApiResponse.<CustomerResponse>builder()
                .result(accountService.updateCustomer(username, request))
                .build();
    }

    @GetMapping("/staff")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ApiResponse<Page<ManagerResponse>> getAllStaff(Pageable pageable) {
        return ApiResponse.<Page<ManagerResponse>>builder()
                .result(accountService.getAllStaff(pageable))
                .build();
    }

    @GetMapping("/staff/search")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ApiResponse<Page<ManagerResponse>> searchStaff(Pageable pageable, @RequestParam(required = false) String keyword) {
        return ApiResponse.<Page<ManagerResponse>>builder()
                .result(accountService.searchStaff(keyword, pageable))
                .build();
    }

    @PostMapping("/staff")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ApiResponse<ManagerResponse> createStaff(@Valid @RequestBody StaffRegistrationRequest request) {
        return ApiResponse.<ManagerResponse>builder()
                .result(accountService.createStaff(request))
                .build();
    }

    @PutMapping("/staff/{username}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ApiResponse<ManagerResponse> updateStaff(
            @PathVariable String username,
            @Valid @RequestBody UpdateStaffRequest request) {
        return ApiResponse.<ManagerResponse>builder()
                .result(accountService.updateStaff(username, request))
                .build();
    }

    @DeleteMapping("/staff/{username}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ApiResponse<String> deleteStaff(@PathVariable String username) {
        accountService.deleteStaff(username);
        return ApiResponse.<String>builder()
                .result("Staff soft deleted successfully")
                .build();
    }

    @PutMapping("/staff/{username}/password")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ApiResponse<String> updateStaffPassword(
            @PathVariable String username,
            @RequestBody ChangePasswordRequest request) {
        accountService.updateStaffPassword(username, request.getNewPassword());
        return ApiResponse.<String>builder()
                .result("Staff password updated successfully")
                .build();
    }
}
