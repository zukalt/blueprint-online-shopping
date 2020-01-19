package com.deepthought.services.common;

import java.util.Date;
import java.util.UUID;

public class Utils {

    public static String randomUUID() {
        return UUID.randomUUID().toString();
    }

    public static Date timeAfterHours(int hours) {
        return new Date(System.currentTimeMillis() + hours * 3_600_000) ;
    }

    public static String exceptionClassToMessageCode(Class<?> clazz) {
        return exceptionClassNameToMessageCode(clazz.getSimpleName());
    }
    public static String exceptionClassNameToMessageCode(String className) {
        return className
                .replaceAll("Exception$", "")
                .replaceAll("([A-Z])", "-$1")
                .substring(1)
                .toLowerCase() ;
    }
}
