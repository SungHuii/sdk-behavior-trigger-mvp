package com.behavior.sdk.trigger.common.exception;

import org.hibernate.service.spi.ServiceException;

public class UnauthorizedOriginException extends ServiceException {
    public UnauthorizedOriginException() {
        super(String.valueOf(ErrorSpec.AUTH_UNAUTHORIZED_ORIGIN));
    }
}
