package com.web.recommendationservice.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthenticationRequest {
    private String username;
    private Collection<? extends GrantedAuthority> authorities;

    public static AuthenticationRequest fromSecurityContext() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        return AuthenticationRequest.builder()
                .username(authentication.getName())
                .authorities(authentication.getAuthorities())
                .build();
    }
}
