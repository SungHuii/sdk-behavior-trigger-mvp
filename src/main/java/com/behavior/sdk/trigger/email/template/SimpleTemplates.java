package com.behavior.sdk.trigger.email.template;

public final class SimpleTemplates {

    private SimpleTemplates() {}

    public static String subjectForGreeting(String displayName) {
        return "VisiLog Notification for " + safe(displayName);
    }

    public static String bodyForGreetingText(String displayName) {
        return """
                Dear %s,

                VisiLog notification.

                from. VisiLog
                """.formatted(safe(displayName));
    }

    private static String safe(String s) {
        return (s == null || s.isBlank()) ? "User" : s;
    }
}
