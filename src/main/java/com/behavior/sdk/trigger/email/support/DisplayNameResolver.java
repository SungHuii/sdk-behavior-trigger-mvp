package com.behavior.sdk.trigger.email.support;

public class DisplayNameResolver {

    private DisplayNameResolver() {
    }

    public static String fromEmailLocalPart(String email) {
        if (email == null || email.isBlank()) return  "User";
        int at = email.indexOf('@');
        String local = (at > 0) ? email.substring(0, at) : email;
        return local.isBlank() ? "User" : local;
    }
}
