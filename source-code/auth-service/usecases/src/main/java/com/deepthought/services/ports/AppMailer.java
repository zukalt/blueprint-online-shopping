package com.deepthought.services.ports;


import com.deepthought.services.ex.UnsuccessfulOperationException;
import com.deepthought.services.model.User;
import com.deepthought.services.model.UserCredential;

public interface AppMailer {

    void sendUserActivationEmail(User user, UserCredential activationToken) throws UnsuccessfulOperationException;
    void sendPasswordResetEmail(User user, UserCredential uc) throws UnsuccessfulOperationException;
}
