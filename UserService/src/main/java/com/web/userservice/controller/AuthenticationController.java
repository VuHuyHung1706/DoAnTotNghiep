package com.web.userservice.controller;

import com.nimbusds.jose.JOSEException;

import com.web.userservice.dto.response.ApiResponse;
import com.web.userservice.dto.response.AuthenticationResponse;
import com.web.userservice.dto.response.IntrospectResponse;
import com.web.userservice.dto.request.AuthenticationRequest;
import com.web.userservice.dto.request.IntrospectRequest;
import com.web.userservice.dto.request.LogoutRequest;
import com.web.userservice.dto.request.RefreshRequest;
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
}
