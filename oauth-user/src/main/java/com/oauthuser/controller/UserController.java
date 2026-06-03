package com.oauthuser.controller;

import com.oauthuser.entity.User;
import com.oauthuser.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/")
    public List<User> getUser() {
        return userService.getAllUsers();
    }
    @PostMapping("/")
    public User addUser(@RequestBody User user) {
        if (userService.addUser(user))
            return user;
        else return null;
    }
}
