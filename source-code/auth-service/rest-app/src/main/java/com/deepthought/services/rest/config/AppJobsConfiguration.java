package com.deepthought.services.rest.config;

import com.deepthought.services.ports.UserSessionStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class AppJobsConfiguration {

    @Autowired
    UserSessionStore sessionStore;

    @Scheduled(fixedRateString = "${app.sessions.keepAliveTimeMs}")
    @Async
    public void pruneTimedOutSessions() {
        sessionStore.cleanExpiredSessions();
    }
}
