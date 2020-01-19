package com.deepthought.services.rest.config;

import com.deepthought.services.model.Role;
import com.deepthought.services.model.User;
import com.deepthought.services.model.UserCredential;
import com.deepthought.services.ports.PasswordEncoder;
import com.deepthought.services.ports.UserPasswordStore;
import com.deepthought.services.ports.UserStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.UUID;


@Configuration
public class AppInitialization {

    @Value("${app.admin.email}")
    private String administratorEmail;

    @Autowired
    private UserStore userStore;
    @Autowired
    private UserPasswordStore passwordStore;

    @Autowired
    private PasswordEncoder pencoder;


    @Bean // @Profile({"dev", "test"})
    CommandLineRunner generateSomeTestUsers() {
        return (args) -> {
            injectUser("bob@example.com", "Bob the Buyer", "bob", Role.BUYER);
            injectUser("susan@example.com", "Susan the Seller", "susan", Role.SELLER);
            injectUser("ada@example.com", "Ada the Moderator", "ada", Role.MODERATOR);
        };
    }

    private void injectUser(String email, String fullName, String password, Role role) {
        if (userStore.findByEmail(email).isPresent()) {
            return;
        }
        userStore.save(new User(UUID.randomUUID().toString(), email
                , fullName
                , role));
        UserCredential uc = new UserCredential(email);
        uc.setEncodedPassword(pencoder.encode(password));
        System.err.println("Generated password for "+email + " is " +  password);
        passwordStore.save(uc);
    }

}
