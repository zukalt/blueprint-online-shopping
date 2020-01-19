package com.deepthought.services.ex;

public class AppBaseException extends Exception{

    public String getMessage() {
        return getClass().getSimpleName();
    }
}
