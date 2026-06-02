package com.demooauth.controller;

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
    @GetMapping("/")
    public String home(OAuth2AuthenticationToken  token) {
        System.out.println("token name : "+token.getName());
        System.out.println("token principals : "+token.getPrincipal());
        System.out.println("token credentials : "+token.getCredentials());
        System.out.println("token details : "+token.getDetails());
        System.out.println("token authorities : "+token.getAuthorities());
        System.out.println("authorized client registration id: "+token.getAuthorizedClientRegistrationId());
        String email = "";
        if (token != null) {
             email = token.getPrincipal().getAttribute("email");
        }
        return "Hello  "+ email;
    }
}
// wwe can check the authorization code , token , auth request and response
// in RestClientAuthorizationCodeTokenResponseClient class