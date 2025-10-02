package com.web.apigateway.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.apigateway.service.IdentityService;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PACKAGE, makeFinal = true)
@Slf4j
public class AuthenticationFilter implements GlobalFilter, Ordered {

    IdentityService identityService;

    ObjectMapper objectMapper;

    @NonFinal
    private String[] publicEndpoints = {
            "/identity/accounts/register",
            "/identity/auth/login"
    };

    @NonFinal
    private String[] getPublicMethodEndpoints = {
            "/movie-service/movies"
    };

    @Value("${app.api-prefix}")
    @NonFinal
    private String apiPrefix;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        List<String> authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION);
        log.info("Entering the authentication gateway filter");

        if (isPublicEndpoint(exchange.getRequest()) || isGetPublicMethodEndpoint(exchange.getRequest()))
        {
            return chain.filter(exchange);
        }

        if (CollectionUtils.isEmpty(authHeader))
        {
            return unauthenticated(exchange.getResponse());
        }
        String token = authHeader.get(0).replace("Bear" , "");
        log.info("Token: {}", token);

        identityService.introspect(token).subscribe(introspectResponseApiResponse
        ->
        {
            log.info("Result: {}", introspectResponseApiResponse.getResult().getValid());
        });
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -1;
    }

    Mono<Void> unauthenticated(ServerHttpResponse response)
    {
        String body = "unauthenticated";
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return response.writeWith(Mono.just(response.bufferFactory().wrap(body.getBytes())));
    }

    private boolean isPublicEndpoint(ServerHttpRequest request){
        return Arrays.stream(publicEndpoints).anyMatch(s-> request.getURI().getPath().matches(apiPrefix + s));
    }

    private boolean isGetPublicMethodEndpoint(ServerHttpRequest request){
        return Arrays.stream(getPublicMethodEndpoints).anyMatch(s-> request.getURI().getPath().matches(apiPrefix + s));
    }
}
