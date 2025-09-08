package com.behavior.sdk.trigger.common.exception;

import java.util.List;

public class ServiceException extends RuntimeException {
    private final ErrorSpec spec;
    private final List<FieldErrorDetail> details;
    private final String hint;

    public ServiceException(ErrorSpec spec) {
        super(spec.koMessage());
        this.spec = spec;
        this.details = null;
        this.hint = null;
    }

    public ServiceException(ErrorSpec spec, String hint, List<FieldErrorDetail> details) {
        super(spec.koMessage());
        this.spec = spec;
        this.hint = hint;
        this.details = details;
    }

    public ErrorSpec getSpec() { return spec; }
    public List<FieldErrorDetail> getDetails() { return details; }
    public String getHint() { return hint; }
}
