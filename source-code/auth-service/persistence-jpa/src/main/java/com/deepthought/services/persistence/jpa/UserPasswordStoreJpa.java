package com.deepthought.services.persistence.jpa;

import com.deepthought.services.model.UserCredential;
import com.deepthought.services.ports.UserPasswordStore;
import org.springframework.data.repository.Repository;

@org.springframework.stereotype.Repository
public interface UserPasswordStoreJpa extends UserPasswordStore, Repository<UserCredential, String> {

}
