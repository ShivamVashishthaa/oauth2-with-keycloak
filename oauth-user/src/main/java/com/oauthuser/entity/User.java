package com.oauthuser.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private String id;
    private String keycloakId;
    private String username;
    private String password;
    private String email;
    private String phone;
    private LocalDateTime createdAt;
}
