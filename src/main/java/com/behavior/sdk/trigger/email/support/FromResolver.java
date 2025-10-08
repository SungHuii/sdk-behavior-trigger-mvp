package com.behavior.sdk.trigger.email.support;

public final class FromResolver {
    private FromResolver() {}

    public static FromSpec resolve(String projectNameOrNull, String allowedDomainOrNull) {
        String addr = (allowedDomainOrNull == null || allowedDomainOrNull.isBlank())
                ? "no-reply@visilog.app"
                : "no-reply@" + allowedDomainOrNull;
        String name = (projectNameOrNull == null || projectNameOrNull.isBlank())
                ? (allowedDomainOrNull == null ? "VisiLog" : allowedDomainOrNull)
                : projectNameOrNull;
        return new FromSpec(addr, name);
    }
}
