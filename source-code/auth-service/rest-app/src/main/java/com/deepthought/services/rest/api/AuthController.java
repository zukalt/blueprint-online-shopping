package com.deepthought.services.rest.api;

import com.deepthought.services.Authentication;
import com.deepthought.services.AuthorizationContext;
import com.deepthought.services.UserAdministration;
import com.deepthought.services.dto.*;
import com.deepthought.services.ex.AppBaseException;
import com.deepthought.services.ex.NotAuthorizedException;
import com.deepthought.services.ex.UnsuccessfulOperationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.http.ResponseEntity.status;


@RestController
@RequestMapping("/external/auth/v1")
public class AuthController extends BaseController {

    private Authentication authentication;
    private UserAdministration userAdministration;


    public AuthController(Authentication authentication, UserAdministration userAdministration) {
        this.authentication = authentication;
        this.userAdministration = userAdministration;
    }

    @PostMapping("/login")
    public ResponseEntity<UserSessionDto> passwordLogin(@RequestParam("email") String email, @RequestParam("password") String password) throws NotAuthorizedException {
        return ok(authentication.passwordLogin(email, password));
    }

    @GetMapping("/logout")
    @ResponseStatus(HttpStatus.OK)
    public void logout() {
        authentication.logout();
    }

    @GetMapping("/session")
    public ResponseEntity<UserSessionDto> session() throws AppBaseException {

        return ok(AuthorizationContext.getUserSession());
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRegistrationRequest request) throws UnsuccessfulOperationException {
        userAdministration.register(request);
        return status(HttpStatus.CREATED).build();
    }

    @PostMapping("/create-password")
    public ResponseEntity<?> createPassword(@RequestBody ResetPasswordRequest request) throws AppBaseException {
        authentication.resetPassword(request);
        return status(HttpStatus.CREATED).build();
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody CreatePasswordRequest request) throws AppBaseException {
        authentication.requestPasswordReset(request);
        return status(HttpStatus.CREATED).build();
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) throws AppBaseException {
        authentication.changePassword(request);
        return ok().build();
    }
}
