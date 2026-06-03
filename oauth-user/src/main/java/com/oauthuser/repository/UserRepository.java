package com.oauthuser.repository;

import com.oauthuser.entity.User;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class UserRepository {
    private static List<User> users = null;

    public List<User> arrayList() {
        if (users == null) {
            users = new ArrayList<>();
        }
        return users;
    }
}
