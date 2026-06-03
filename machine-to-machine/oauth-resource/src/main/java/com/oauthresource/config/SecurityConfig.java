package com.oauthresource.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collection;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().authenticated())

//        Ye line application ko JWT Resource Server bana deti hai.
                .oauth2ResourceServer(resource -> resource
                        .jwt(Customizer.withDefaults()))
                .build();
    }

    //    @Bean
    public JwtDecoder jwtDecoder() {
        // Manually create JWT decoder with issuer URI
        return NimbusJwtDecoder.withIssuerLocation("http://localhost:8085/realms/oauth2").build();
    }

    //    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter());
        return converter;
    }

    //    @Bean
    public Converter<Jwt, Collection<GrantedAuthority>> jwtGrantedAuthoritiesConverter() {
        JwtGrantedAuthoritiesConverter converter = new JwtGrantedAuthoritiesConverter();
        converter.setAuthorityPrefix("ROLE_");
        return converter;
    }
}

//Internally Spring ye beans create karta hai:
//1. BearerTokenAuthenticationFilter
//2. JwtAuthenticationProvider
//3. NimbusJwtDecoder


// "jwks_uri": "...",

/*
        Request
           │
           ▼
        BearerTokenAuthenticationFilter     Intercept request to get token
           │
           ▼
        Fetch JWT                           Extract token from header
           │
           ▼
        JwtDecoder                          To Check token format is correct, expiration, issuer, etc.
           │
           ▼
        Keycloak public key verify          Most Important Resource server already have public key of keycloak
           │                                Token is signed with private key of Keycloak. so resource server
           ▼                                use public key to verify the signature of token of identify that
                                            token is issued by keycloak or not
        Authentication create               Now create the authentication object and add roles and authorities
                                            in it and allow request
 */
/*
    Important point:
        1.Resource server auth server se baat nahi karta har request par.
        2.Public key local mein cache hoti hai, isliye fast hai.

        Ek line mein: Resource server client ke token ko public key se verify karta hai bina auth server
        ko call kiye, trust signature based hota hai.
 */