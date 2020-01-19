package com.deepthought.services;

import com.deepthought.services.dto.DtoMapper;
import com.deepthought.services.dto.UserSessionDto;
import com.deepthought.services.ex.AppBaseException;
import com.deepthought.services.ex.NotAuthorizedException;
import com.deepthought.services.model.UserSession;
import com.deepthought.services.ports.UserSessionStore;
import org.mapstruct.factory.Mappers;

public class AuthorizationContext {

    private static DtoMapper convert = Mappers.getMapper(DtoMapper.class);
    private UserSessionStore sessionStore;
    private static ThreadLocal<UserSession> currentSession = new ThreadLocal<>();

    @FunctionalInterface
    public interface Function<T> {
        T call() throws AppBaseException;
    }

    @FunctionalInterface
    public interface VoidFunction {
        void call() throws AppBaseException;
    }


    public AuthorizationContext(UserSessionStore sessionStore) {
        this.sessionStore = sessionStore;
    }

    static UserSession currentSession() throws NotAuthorizedException {
        UserSession session = currentSession.get();
        if (session == null) {
            throw new NotAuthorizedException();
        }
        return session;
    }

    public static UserSessionDto getUserSession() throws NotAuthorizedException {
        return convert.toSessionDto(currentSession());
    }
    public void startSession(String token) {
        if (token != null) {
            currentSession.set(sessionStore.findByToken(token).orElse(null));
        }
    }

    public void endSession() {
        currentSession.remove();
    }

    public <T> T runInContext(String token, Function<T> supplier) throws AppBaseException {
        try {
            startSession(token);
            return supplier.call();
        } finally {
            endSession();
        }
    }

    public void runInContext(String token, VoidFunction runnable) throws AppBaseException {
        try {
            startSession(token);
            runnable.call();
        } finally {
            endSession();
        }
    }


}
