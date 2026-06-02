package com.oauthresource.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DataController {
    @GetMapping("/data")
    public String data(){
        return "Hello from resource server";
    }
}
