package com.web.recommendationservice.repository.client;

import com.web.recommendationservice.dto.request.IntrospectRequest;
import com.web.recommendationservice.dto.response.ApiResponse;
import com.web.recommendationservice.dto.response.IntrospectResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "identity-service", url = "http://172.30.1.10:8080")
public interface IdentityServiceClient {
    @PostMapping("/auth/introspect")
    ApiResponse<IntrospectResponse> introspect(@RequestBody IntrospectRequest request);
}
