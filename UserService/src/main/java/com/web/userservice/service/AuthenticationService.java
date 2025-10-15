package com.web.userservice.service;


import com.nimbusds.jose.JOSEException;
import com.web.userservice.dto.response.AuthenticationResponse;
import com.web.userservice.dto.response.IntrospectResponse;
import com.web.userservice.dto.resquest.AuthenticationRequest;
import com.web.userservice.dto.resquest.IntrospectRequest;
import com.web.userservice.dto.resquest.LogoutRequest;
import com.web.userservice.dto.resquest.RefreshRequest;

import java.text.ParseException;

public interface AuthenticationService {
    AuthenticationResponse authenticate(AuthenticationRequest request);
    AuthenticationResponse googleLogin(String code);
    IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException;
    void logout(LogoutRequest request) throws ParseException, JOSEException;
    AuthenticationResponse refresh(RefreshRequest request) throws ParseException, JOSEException;
}
