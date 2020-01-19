package com.deepthought.services.rest.config;

import com.deepthought.services.Authentication;
import com.deepthought.services.AuthorizationContext;
import com.deepthought.services.UserAdministration;
import com.deepthought.services.impl.FakeMailer;
import com.deepthought.services.impl.SessionInMemoryStoreImpl;
import com.deepthought.services.ports.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.util.DigestUtils;

@Configuration
@EnableAsync
public class AppConfiguration {

    @Value("${app.sessions.keepAliveTimeMs}")
    private long sessionExpirationTimeMilis = 3600_000L;

    @Value("${app.sessions.tokensKeepAliveInHours}")
    private int tokensValidityPeriodHours = 6;

    @Bean
    AuthorizationContext authorizationContext(UserSessionStore sessionStore) {
        return new AuthorizationContext(sessionStore);
    }

    @Bean
    Authentication authorization(PasswordEncoder passwordEncoder,
                                 UserPasswordStore passwordStore,
                                 UserStore userStore,
                                 UserSessionStore userSessionStore,
                                 AppMailer mailer) {
        return new Authentication(passwordEncoder, passwordStore, userStore, userSessionStore, mailer, tokensValidityPeriodHours);
    }

    @Bean
    UserAdministration userAdministration(UserStore userStore, Authentication authentication, AppMailer mailer) {
        return new UserAdministration(userStore, authentication, mailer);
    }

    @Bean
    UserSessionStore userSessionStore() {
        return new SessionInMemoryStoreImpl(sessionExpirationTimeMilis);
    }


    @Bean
    @ConditionalOnMissingBean
    AppMailer fakeAppMailer() {
        return new FakeMailer();
    }



    @Bean
    PasswordEncoder passwordEncoder() {
        return new PasswordEncoder() {

            @Override
            public String encode(String password) {
                return DigestUtils.md5DigestAsHex(password.getBytes());
            }

            @Override
            public boolean matches(String encodedPass, String providedPass) {
                return encodedPass.equals(DigestUtils.md5DigestAsHex(providedPass.getBytes()));
            }
        };
    }


}
