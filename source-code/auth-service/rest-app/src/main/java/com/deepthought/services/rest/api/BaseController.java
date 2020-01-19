package com.deepthought.services.rest.api;

import com.deepthought.services.ex.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

import java.util.Collections;
import java.util.Map;

import static org.springframework.http.ResponseEntity.status;

public abstract class BaseController {

    @ExceptionHandler(NotAuthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public void sendNotAuthorized() {
    }

    @ExceptionHandler(UnsuccessfulOperationException.class)
    public ResponseEntity<?> unsuccessfulOperation(UnsuccessfulOperationException ex, WebRequest request) {
        return status(HttpStatus.CONFLICT).body(responseFromException(ex));
    }
    @ExceptionHandler({UserNotFoundException.class})
    public ResponseEntity<?> notFound(UnsuccessfulOperationException ex, WebRequest request) {
        return status(HttpStatus.NOT_FOUND).body(responseFromException(ex));
    }
    @ExceptionHandler(NotAllowedException.class)
    public ResponseEntity<?> forbidden(NotAllowedException ex, WebRequest request) {
        return status(HttpStatus.FORBIDDEN).body(responseFromException(ex));
    }

    private Map<String, String> responseFromException(AppBaseException ex) {
        return Collections.singletonMap("message", ex.getMessage());
    }
}
