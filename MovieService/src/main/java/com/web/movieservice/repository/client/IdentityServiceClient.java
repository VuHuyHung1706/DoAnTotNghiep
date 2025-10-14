package com.web.movieservice.repository.client;

import com.web.movieservice.dto.response.ApiResponse;
import com.web.movieservice.dto.response.CustomerResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "identity-service", url = "${user.service.url}")
public interface IdentityServiceClient {

    @GetMapping("accounts/customer/{id}")
    ApiResponse<CustomerResponse> getUserById(@PathVariable Integer id);
}
