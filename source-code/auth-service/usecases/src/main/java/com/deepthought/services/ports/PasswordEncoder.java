package com.deepthought.services.ports;

public interface PasswordEncoder {

    String encode(String password) ;
    boolean matches(String encodedPass, String providedPass) ;
}
