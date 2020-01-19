package com.deepthought.services.ports;

import com.deepthought.services.model.UserCredential;

import java.util.Optional;

public interface UserPasswordStore {

    Optional<UserCredential> getByEmail(String email) ;
    void save(UserCredential userCredential) ;
}
