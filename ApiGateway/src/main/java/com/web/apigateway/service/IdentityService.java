package com.web.apigateway.service;

import com.web.apigateway.dto.request.IntrospectRequest;
import com.web.apigateway.dto.respose.ApiResponse;
import com.web.apigateway.dto.respose.IntrospectResponse;
import com.web.apigateway.responsitory.IdentityClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class IdentityService {
    IdentityClient identityClient;
    public Mono<ApiResponse<IntrospectResponse>> introspect(String token)
    {
        return identityClient.introspect(IntrospectRequest.builder().token(token).build());
    }
}
