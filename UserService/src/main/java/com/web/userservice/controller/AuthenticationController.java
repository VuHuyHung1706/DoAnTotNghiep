package com.web.userservice.controller;

import com.nimbusds.jose.JOSEException;

import com.web.userservice.dto.response.ApiResponse;
import com.web.userservice.dto.response.AuthenticationResponse;
import com.web.userservice.dto.response.IntrospectResponse;
import com.web.userservice.dto.request.AuthenticationRequest;
import com.web.userservice.dto.request.ForgotPasswordRequest;
import com.web.userservice.dto.request.IntrospectRequest;
import com.web.userservice.dto.request.LogoutRequest;
import com.web.userservice.dto.request.RefreshRequest;
import com.web.userservice.dto.request.VerifyOtpForgotPasswordRequest;
import com.web.userservice.dto.request.ResetPasswordRequest;
import com.web.userservice.dto.request.VerifyUsernameRequest;
import com.web.userservice.dto.response.VerifyUsernameResponse;
import com.web.userservice.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;


    @GetMapping("/callback")
    public ApiResponse<String> callback() {
        String result = "callback";
        return ApiResponse.<String>builder()
                .result(result)
                .build();
    }


    @PostMapping("/login")
    public ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        var result = authenticationService.authenticate(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .result(result)
                .build();
    }


    @PostMapping("/google/login")
    public ApiResponse<AuthenticationResponse> googleLogin(@RequestParam String code) {
        var result = authenticationService.googleLogin(code);
        return ApiResponse.<AuthenticationResponse>builder()
                .result(result)
                .build();
    }

    @PostMapping("/introspect")
    public ApiResponse<IntrospectResponse> introspect(@RequestBody IntrospectRequest request) throws ParseException, JOSEException {
        var result = authenticationService.introspect(request);
        return ApiResponse.<IntrospectResponse>builder()
                .result(result)
                .build();
    }

    @PostMapping("/refresh")
    public ApiResponse<AuthenticationResponse> refresh(@RequestBody RefreshRequest request)
            throws ParseException, JOSEException {
        var result = authenticationService.refresh(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .result(result)
                .build();
    }


    @PostMapping("/logout")
    public ApiResponse<String> logout(@RequestBody LogoutRequest request) throws ParseException, JOSEException {
        authenticationService.logout(request);
        return ApiResponse.<String>builder()
                .result("Logout successful")
                .build();
    }

    @PostMapping("/verify-username")
    public ApiResponse<VerifyUsernameResponse> verifyUsername(@RequestBody VerifyUsernameRequest request) {
        var result = authenticationService.verifyUsername(request.getUsername());
        return ApiResponse.<VerifyUsernameResponse>builder()
                .result(result)
                .build();
    }

    @PostMapping("/forgot-password")
    public ApiResponse<String> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        authenticationService.forgotPassword(request.getEmail());
        return ApiResponse.<String>builder()
                .result("OTP has been sent to your email")
                .build();
    }

    @PostMapping("/verify-otp-forgot-password")
    public ApiResponse<String> verifyOtpForgotPassword(@RequestBody VerifyOtpForgotPasswordRequest request) {
        String resetToken = authenticationService.verifyOtpAndGenerateResetToken(
                request.getEmail(),
                request.getOtp()
        );
        return ApiResponse.<String>builder()
                .result("OTP verified. Check your email for reset password link.")
                .build();
    }

    @PostMapping("/reset-password")
    public ApiResponse<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        authenticationService.resetPassword(request.getToken(), request.getNewPassword());
        return ApiResponse.<String>builder()
                .result("Password has been reset successfully")
                .build();
    }
}
