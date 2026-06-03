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
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
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

    //    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        // client registration - who is the client
        ClientRegistration oauth2_client_credentials = ClientRegistration
                .withRegistrationId("keycloak-client")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .clientId("oauth2-client-credentials")
                .clientSecret("your-client-secret")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
//                Redirect URI only use in authorization_code flow, at user login.
//                .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                .redirectUri("http://localhost:8083/login/oauth2/code/keycloak-client")
                .scope("openid", "profile", "email")

                // client provider - where is auth server
                .authorizationUri("http://localhost:8085/realms/oauth2/protocol/openid-connect/auth")
                .tokenUri("http://localhost:8085/realms/oauth2/protocol/openid-connect/token")
                .userInfoUri("http://localhost:8085/realms/oauth2/protocol/openid-connect/userinfo")
                .jwkSetUri("http://localhost:8085/realms/oauth2/protocol/openid-connect/certs")
                .issuerUri("http://localhost:8085/realms/oauth2")
                .userNameAttributeName("sub")
                .build();

        ClientRegistration keycloakClientCredentials = ClientRegistration
                .withRegistrationId("keycloak-client")
                .clientId("oauth2-client-credentials")
                .clientSecret("StGXk1k7Dc0C5pH14JG1y87w07qa39Bh")
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)  // Changed
                .scope("openid")
                .tokenUri("http://localhost:8085/realms/oauth2/protocol/openid-connect/token")
                .build();

        return new InMemoryClientRegistrationRepository(oauth2_client_credentials, keycloakClientCredentials);
    }

}
