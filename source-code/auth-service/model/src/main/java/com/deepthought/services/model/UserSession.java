package com.deepthought.services.model;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
public class UserSession {
    private String token;
    private User user;
    private long validTill;

    public UserSession(User user) {
        this.token = UUID.randomUUID().toString() ;
        this.user = user;
    }

}
