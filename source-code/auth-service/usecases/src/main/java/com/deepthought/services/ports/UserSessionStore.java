package com.deepthought.services.ports;


import com.deepthought.services.model.UserSession;

import java.util.Optional;

public interface UserSessionStore {

    UserSession save(UserSession session) ;
    void removeByToken(String sessionToken) ;
    Optional<UserSession> findByToken(String token) ;
    void cleanExpiredSessions();
}
