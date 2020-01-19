package com.deepthought.services;


import com.deepthought.services.common.Utils;
import com.deepthought.services.dto.*;
import com.deepthought.services.ex.*;
import com.deepthought.services.model.User;
import com.deepthought.services.model.UserCredential;
import com.deepthought.services.model.UserSession;
import com.deepthought.services.ports.*;
import org.mapstruct.factory.Mappers;

import java.util.Optional;

public class Authentication {

    private PasswordEncoder passwordEncoder;
    private UserPasswordStore passwordStore;
    private UserStore userStore;
    private UserSessionStore userSessionStore;
    private AppMailer mailer;
    private int tokenValidationTimeInHours;
    private DtoMapper convert = Mappers.getMapper(DtoMapper.class);

    public Authentication(PasswordEncoder passwordEncoder,
                          UserPasswordStore passwordStore,
                          UserStore userStore,
                          UserSessionStore userSessionStore,
                          AppMailer mailer,
                          int tokenValidationTimeInHours) {
        this.passwordEncoder = passwordEncoder;
        this.passwordStore = passwordStore;
        this.userStore = userStore;
        this.userSessionStore = userSessionStore;
        this.mailer = mailer;
        this.tokenValidationTimeInHours = tokenValidationTimeInHours;
    }

    public UserSessionDto passwordLogin(String email, String password) throws NotAuthorizedException {
        return passwordStore.getByEmail(email)
                .filter(uc -> passwordEncoder.matches(uc.getEncodedPassword(), password))
                .map(uc -> userStore.findByEmail(email))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(UserSession::new)
                .map(session->userSessionStore.save(session))
                .map(convert::toSessionDto)
                .orElseThrow(NotAuthorizedException::new);
    }

    UserCredential createPassword(User user) {
        return createOrResetPasswordResetToken(user);
    }

    public void requestPasswordReset(CreatePasswordRequest createPasswordRequest) throws UnsuccessfulOperationException {
        Optional<User> user = userStore.findByEmail(createPasswordRequest.email);
        if (user.isPresent()) {
            UserCredential userCredential = createOrResetPasswordResetToken(user.get());
            mailer.sendPasswordResetEmail(user.get(), userCredential);
        }
    }

    public void resetPassword(ResetPasswordRequest resetPasswordRequest) throws PasswordResetFailedException {
        UserCredential credential = passwordStore
                .getByEmail(resetPasswordRequest.email)
                .filter( uc-> resetPasswordRequest.token.equals(uc.getPasswordResetToken()))
                .orElseThrow(PasswordResetFailedException::new) ;

        credential.setTokenValidTill(null);
        credential.setPasswordResetToken(null);
        credential.setEncodedPassword(passwordEncoder.encode(resetPasswordRequest.newPassword));
        passwordStore.save(credential);
    }

    private UserCredential createOrResetPasswordResetToken(User user) {
        UserCredential userCredential = passwordStore
                .getByEmail(user.getEmail())
                .orElse(new UserCredential(user.getEmail()));

        userCredential.setPasswordResetToken(Utils.randomUUID());
        userCredential.setTokenValidTill(Utils.timeAfterHours(tokenValidationTimeInHours));

        passwordStore.save(userCredential);
        return userCredential;
    }

    public void logout() {
        try {
            userSessionStore.removeByToken(AuthorizationContext.currentSession().getToken());
        } catch (NotAuthorizedException e) {
            // if there is no session, then keep quiet
        }
    }

    public void changePassword(ChangePasswordRequest request) throws NotAuthorizedException, UserNotFoundException, NotAllowedException {
        String email = AuthorizationContext.currentSession().getUser().getEmail();
        UserCredential userCredential = passwordStore
                .getByEmail(email)
                .orElseThrow(UserNotFoundException::new);

        if (passwordEncoder.matches(userCredential.getEncodedPassword(), request.oldPassword)) {
            userCredential.setEncodedPassword(passwordEncoder.encode(request.newPassword));
            passwordStore.save(userCredential);
        }
        else {
            throw new NotAllowedException();
        }
    }
}
