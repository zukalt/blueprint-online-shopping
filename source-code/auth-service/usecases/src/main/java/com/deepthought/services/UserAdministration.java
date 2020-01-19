package com.deepthought.services;


import com.deepthought.services.common.Utils;
import com.deepthought.services.dto.DtoMapper;
import com.deepthought.services.dto.UserRegistrationRequest;
import com.deepthought.services.ex.UnsuccessfulOperationException;
import com.deepthought.services.ex.UserAlreadyExistsException;
import com.deepthought.services.model.Role;
import com.deepthought.services.model.User;
import com.deepthought.services.ports.AppMailer;
import com.deepthought.services.ports.UserStore;
import org.mapstruct.factory.Mappers;


public class UserAdministration {

    private UserStore userStore;
    private Authentication authentication;
    private AppMailer mailer;
    private DtoMapper convert = Mappers.getMapper(DtoMapper.class);

    public UserAdministration(UserStore userStore, Authentication authentication, AppMailer mailer) {
        this.userStore = userStore;
        this.authentication = authentication;
        this.mailer = mailer;
    }

    public void register(UserRegistrationRequest userInfo) throws UnsuccessfulOperationException {
        if (userStore.findByEmail(userInfo.email).isPresent()) {
            throw new UserAlreadyExistsException();
        }
        User user = convert.toUser(userInfo);
        user.setId(Utils.randomUUID());
        user.setRole(Role.BUYER);
        userStore.save(user);
        mailer.sendUserActivationEmail(user, authentication.createPassword(user));
    }
}
