package com.lcwd.user.service.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

/** Protects UserService even when a caller bypasses API Gateway. */
@Component
public class JwtServiceAuthenticationFilter extends OncePerRequestFilter {
    private final LoadBalancerClient loadBalancerClient;
    private final String jwtServiceId;
    private final RestClient restClient = RestClient.create();

    public JwtServiceAuthenticationFilter(LoadBalancerClient loadBalancerClient,
                                          @Value("${app.security.jwt-service-id}") String jwtServiceId) {
        this.loadBalancerClient = loadBalancerClient;
        this.jwtServiceId = jwtServiceId;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return "OPTIONS".equalsIgnoreCase(request.getMethod()) || "/actuator/health".equals(request.getServletPath());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorization == null || !authorization.startsWith("Bearer ") || authorization.length() <= 7) {
            reject(response, HttpStatus.UNAUTHORIZED, "A Bearer token is required");
            return;
        }
        try {
            ServiceInstance jwtService = loadBalancerClient.choose(jwtServiceId);
            restClient.post().uri(UriComponentsBuilder.fromUri(jwtService.getUri()).path("/auth/validate").build().toUri())
                    .header(HttpHeaders.AUTHORIZATION, authorization).retrieve().toBodilessEntity();
            filterChain.doFilter(request, response);
        } catch (RestClientException exception) {
            reject(response, HttpStatus.UNAUTHORIZED, "Invalid or expired token");
        } catch (RuntimeException exception) {
            reject(response, HttpStatus.SERVICE_UNAVAILABLE, "Authentication service is unavailable");
        }
    }

    private void reject(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json");
        response.getWriter().write("{\"status\":" + status.value() + ",\"error\":\"" + message + "\"}");
    }
}
