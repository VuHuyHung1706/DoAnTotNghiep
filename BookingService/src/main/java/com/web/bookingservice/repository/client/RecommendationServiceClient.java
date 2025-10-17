package com.web.bookingservice.repository.client;

import com.web.bookingservice.config.AuthenticationRequest;
import com.web.bookingservice.dto.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "recommendation-service", url = "${recommendation.service.url}", configuration = AuthenticationRequest.class)
public interface RecommendationServiceClient {
    @PostMapping("/recommendations/update-preferences/{username}")
    public ApiResponse<Void> updatePreferences(@PathVariable String username);
}

