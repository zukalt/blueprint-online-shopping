package com.deepthought.services.impl;

import com.deepthought.services.common.Utils;
import com.deepthought.services.model.UserSession;
import com.deepthought.services.ports.UserSessionStore;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class SessionInMemoryStoreImpl implements UserSessionStore {

    private long sessionExpirationTime;
    private Map<String, UserSession> store = new ConcurrentHashMap<>(1000);

    public SessionInMemoryStoreImpl(long sessionExpirationTime) {
        this.sessionExpirationTime = sessionExpirationTime;
    }

    @Override
    public UserSession save(UserSession session) {
        if (session.getToken() == null) {
            session.setToken(Utils.randomUUID());
        }

        store.put(session.getToken(), touch(session));
        return session;
    }

    @Override
    public void removeByToken(String sessionToken) {
        store.remove(sessionToken);
    }

    @Override
    public Optional<UserSession> findByToken(String token) {
        UserSession session = store.get(token);
        if (session != null) {
            if (isExpired(session)) {
                store.remove(token) ;
                session = null;
            }
            else {
                touch(session);
            }
        }
        return Optional.ofNullable(session);
    }

    private UserSession touch(UserSession session) {
        session.setValidTill(System.currentTimeMillis() + sessionExpirationTime);
        return session;
    }

    public void cleanExpiredSessions() {
        store.values().stream()
                .filter(SessionInMemoryStoreImpl::isExpired)
                .forEach(session -> store.remove(session.getToken()));
    }

    private static boolean isExpired(UserSession session) {
        return session.getValidTill() < System.currentTimeMillis();
    }
}
