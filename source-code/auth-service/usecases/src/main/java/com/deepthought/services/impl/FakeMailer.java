package com.deepthought.services.impl;

import com.deepthought.services.model.User;
import com.deepthought.services.model.UserCredential;
import com.deepthought.services.ports.AppMailer;

import java.text.MessageFormat;

public class FakeMailer implements AppMailer {

    User user;
    UserCredential userCredential;

    @Override
    public void sendUserActivationEmail(User user, UserCredential uc) {
        this.user = user;
        this.userCredential = uc;
        System.out.println("Sending activation email to "+ uc.getEmail() + ", token = "
                + uc.getPasswordResetToken()
                +", valid till = " + uc.getTokenValidTill());
        printURL(uc);
    }

    @Override
    public void sendPasswordResetEmail(User user, UserCredential uc) {
        this.user = user;
        this.userCredential = uc;
        System.out.println("Sending password reset to "+ uc.getEmail() + ", token = "
                + uc.getPasswordResetToken()
                +", valid till = " + uc.getTokenValidTill());
        printURL(uc);
    }

    private void printURL(UserCredential uc) {
        System.out.println(MessageFormat.format("http://localhost:8080/auth/create-password?token={0}&email={1}", uc.getPasswordResetToken(), uc.getEmail()));
    }

    public User getLastMailSentUser() {
        return user;
    }

    public UserCredential getLastMailSentUserCredential() {
        return userCredential;
    }

    public void clean() {
        this.user = null;
        this.userCredential = null;
    }
}
