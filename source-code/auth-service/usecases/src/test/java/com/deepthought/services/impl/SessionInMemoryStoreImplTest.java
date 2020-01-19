package com.deepthought.services.impl;

import com.deepthought.services.model.Role;
import com.deepthought.services.model.User;
import com.deepthought.services.model.UserSession;
import com.deepthought.services.ports.UserSessionStore;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


import java.util.Collections;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

public class SessionInMemoryStoreImplTest {

    private UserSessionStore sessionStore;
    private User dummyUser = new User("id", "email", "fullName", Role.BUYER);

    @Before
    public void before() {
        sessionStore = new SessionInMemoryStoreImpl(500);
    }

    @Test
    public void tokenGeneration() {
        UserSession session = new UserSession(dummyUser);
        String token = session.getToken();
        session = sessionStore.save(session);
        assertEquals("Token was updated", token, session.getToken());

        session.setToken(null);
        session = sessionStore.save(session);
        assertNotNull("Token was not generated", session.getToken());
    }

    @Test
    public void sessionExpiration() throws InterruptedException {
        UserSession session = new UserSession(dummyUser);
        String token = session.getToken();
        session = sessionStore.save(session);
        Thread.sleep(501);
        assertFalse("Should return null for expired tokens", sessionStore.findByToken(token).isPresent());

        sessionStore.save(session);
        assertTrue("Saving expired sessions should prolong expiration time", sessionStore.findByToken(token).isPresent());

        Thread.sleep(300);
        sessionStore.findByToken(session.getToken());
        Thread.sleep(300);
        assertTrue("Accessing sessions should prolong expiration time", sessionStore.findByToken(token).isPresent());

        Thread.sleep(501);
        assertFalse("Should return null for expired tokens", sessionStore.findByToken(token).isPresent());
    }

    @Test
    public void concurrencyTest() {
        int createdSessions = 10000;
        long count = IntStream
                .range(0, createdSessions)
                .parallel()
                // create sessions
                .mapToObj((i) -> new UserSession(new User("#" + i, null, null, null)))
                // save them
                .map((session) -> sessionStore.save(session))
                // refresh or remove
                .peek(session -> {
                    int id = Integer.parseInt(session.getUser().getId().substring(1));
                    if (id % 2 == 0) {
                        sessionStore.findByToken(session.getToken())
                                .orElseThrow(() -> new AssertionError("Failed to find stored session"));
                    } else {
                        sessionStore.removeByToken(session.getToken());
                    }
                })
                .filter(session -> sessionStore.findByToken(session.getToken()).isPresent())
                .count();
        assertEquals("Half of sessions should remain after parallel opperations", createdSessions / 2, count);
    }
}