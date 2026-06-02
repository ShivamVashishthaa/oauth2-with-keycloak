package com.pureclient.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.web.client.RestTemplate;

@Configuration
public class BeanConfig {
    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

    @Bean
    public OAuth2AuthorizedClientService getOAuth2AuthorizedClientService(
            ClientRegistrationRepository clientRegistrationRepository) {
        return new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository);
    }

    @Bean
    public OAuth2AuthorizedClientManager oAuth2AuthorizedClientManager(
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientService oAuth2AuthorizedClientService
    ) {
        AuthorizedClientServiceOAuth2AuthorizedClientManager manager =
                new AuthorizedClientServiceOAuth2AuthorizedClientManager
                        (clientRegistrationRepository, oAuth2AuthorizedClientService);

        OAuth2AuthorizedClientProvider provider = OAuth2AuthorizedClientProviderBuilder.builder()
                .clientCredentials()
                .build();
        manager.setAuthorizedClientProvider(provider);
        return manager;
    }


    @Bean
    public CommandLineRunner run(
            OAuth2AuthorizedClientManager oAuth2AuthorizedClientManager,
            RestTemplate restTemplate,
            @Value("${resource.server.url}") String resourceServerUrl
    ) {
        return args -> {
            var authRequest = OAuth2AuthorizeRequest
                    .withClientRegistrationId("keycloak-client")
                    .principal("machine")
                    .build();
            var client = oAuth2AuthorizedClientManager.authorize(authRequest);
            String tokenValue = client.getAccessToken().getTokenValue();
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setBearerAuth(tokenValue);

            ResponseEntity<String> exchange = restTemplate.exchange(
                    resourceServerUrl + "/data",
                    HttpMethod.GET,
                    new HttpEntity<>(httpHeaders),
                    String.class
            );
            System.out.println("Response from service 2 :  " + exchange.getBody());
        };
    }

}
