package com.oauthclientwithresourceserver.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class Service2Client {
    private final RestTemplate restTemplate;
    private final OAuth2AuthorizedClientManager manager;

    @Value("${resource.server.url}")
    String resourceServerUrl;

    public Service2Client(RestTemplate restTemplate, OAuth2AuthorizedClientManager manager) {
        this.restTemplate = restTemplate;
        this.manager = manager;
    }

// this Method Generate a new token then send it to resource server
    public String fetchData1() {
        // Step A: Token lene ka request banao
        OAuth2AuthorizeRequest authRequest = OAuth2AuthorizeRequest
                .withClientRegistrationId("keycloak-client")  // Kaunsa client config use karna hai
                .principal("machine")                          // Machine-to-machine (no user)
                .build();

        // Step B: Manager se token lo (auto fetch/refresh/cache)
        OAuth2AuthorizedClient client = manager.authorize(authRequest);
        String tokenValue = client.getAccessToken().getTokenValue();

        // Step C: Token ke saath resource server call karo
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(tokenValue);  // Authorization: Bearer <token>

        ResponseEntity<String> exchange = restTemplate.exchange(
                resourceServerUrl + "/data",  // http://localhost:8082/data
                HttpMethod.GET,
                new HttpEntity<>(httpHeaders),
                String.class
        );

        return exchange.getBody();  // Return data from resource server
    }

// In this method we use same token
    public String fetchData() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication auth = context.getAuthentication();
        String incomingToken = null;
        if (auth instanceof JwtAuthenticationToken token) {
            incomingToken = token.getToken().getTokenValue();
        }
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(incomingToken);  // Authorization: Bearer <token>

        ResponseEntity<String> exchange = restTemplate.exchange(
                resourceServerUrl + "/data",  // http://localhost:8082/data
                HttpMethod.GET,
                new HttpEntity<>(httpHeaders),
                String.class
        );
        return exchange.getBody();  // Return data from resource server
    }

}
