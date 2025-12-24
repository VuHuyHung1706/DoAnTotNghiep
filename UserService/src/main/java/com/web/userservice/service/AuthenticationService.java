package com.web.userservice.service;


import com.nimbusds.jose.JOSEException;
import com.web.userservice.dto.response.AuthenticationResponse;
import com.web.userservice.dto.response.IntrospectResponse;
import com.web.userservice.dto.response.VerifyUsernameResponse;
import com.web.userservice.dto.request.AuthenticationRequest;
import com.web.userservice.dto.request.IntrospectRequest;
import com.web.userservice.dto.request.LogoutRequest;
import com.web.userservice.dto.request.RefreshRequest;

import java.text.ParseException;

public interface AuthenticationService {
    AuthenticationResponse authenticate(AuthenticationRequest request);
    AuthenticationResponse googleLogin(String code);
    IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException;
    void logout(LogoutRequest request) throws ParseException, JOSEException;
    AuthenticationResponse refresh(RefreshRequest request) throws ParseException, JOSEException;
    VerifyUsernameResponse verifyUsername(String username);
    void forgotPassword(String email);
    String verifyOtpAndGenerateResetToken(String email, String otp);
    void resetPassword(String token, String newPassword);
}
