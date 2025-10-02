package com.web.apigateway.configuration;

import com.web.apigateway.responsitory.IdentityClient;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Component
public class WebClientConfiguration {
    @Bean
    WebClient webClient()
    {
        return  WebClient.builder().baseUrl("http://localhost:8080/").build();
    }

    @Bean
    IdentityClient identityClient(WebClient webClient){
        HttpServiceProxyFactory httpServiceProxyFactory = HttpServiceProxyFactory.builderFor(WebClientAdapter.create(webClient)).build();
        return  httpServiceProxyFactory.createClient(IdentityClient.class);
    }
}
