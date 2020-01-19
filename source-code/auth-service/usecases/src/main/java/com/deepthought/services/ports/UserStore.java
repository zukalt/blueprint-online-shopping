package com.deepthought.services.ports;

import com.deepthought.services.model.User;

import java.util.Optional;

public interface UserStore {
    Optional<User> findByEmail(String email) ;
    void save(User user);
}
