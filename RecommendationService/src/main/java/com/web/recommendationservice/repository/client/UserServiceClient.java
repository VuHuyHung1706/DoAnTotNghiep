package com.web.recommendationservice.repository.client;

import com.web.recommendationservice.dto.response.ApiResponse;
import com.web.recommendationservice.dto.response.CustomerResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "user-service", url = "${user.service.url}")
public interface UserServiceClient {

    @GetMapping("/accounts/customer/{username}")
    ApiResponse<CustomerResponse> getCustomerByUsername(@PathVariable String username);

    @GetMapping("/accounts/customers")
    ApiResponse<List<CustomerResponse>> getAllCustomers();
}
