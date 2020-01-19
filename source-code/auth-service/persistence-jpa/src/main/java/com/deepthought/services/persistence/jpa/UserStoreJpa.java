package com.deepthought.services.persistence.jpa;

import com.deepthought.services.model.User;
import com.deepthought.services.ports.UserStore;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

@org.springframework.stereotype.Repository
public interface UserStoreJpa extends UserStore, Repository<User, String> {

}
