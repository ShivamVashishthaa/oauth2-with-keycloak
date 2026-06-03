package com.oauthuser.service;

import com.oauthuser.entity.User;
import com.oauthuser.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.arrayList();
    }

    public boolean addUser(User user) {
        return userRepository.arrayList().add(user);
    }
}
