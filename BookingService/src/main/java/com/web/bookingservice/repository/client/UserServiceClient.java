package com.web.bookingservice.repository.client;

import com.web.bookingservice.dto.response.ApiResponse;
import com.web.bookingservice.dto.response.CustomerResponse;
import com.web.bookingservice.dto.response.SeatResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = "${user.service.url}")
public interface UserServiceClient {
    @GetMapping("/accounts/customer/{username}")
    ApiResponse<CustomerResponse> getCustomerByUsername(@PathVariable String username);
}
