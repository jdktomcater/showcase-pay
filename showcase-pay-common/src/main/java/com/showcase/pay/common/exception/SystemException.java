package com.showcase.pay.common.exception;

/**
 * System exception for unexpected system-level errors.
 */
public class SystemException extends RuntimeException {

    private Integer code;

    public SystemException(String message) {
        super(message);
        this.code = 500;
    }

    public SystemException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public SystemException(String message, Throwable cause) {
        super(message, cause);
        this.code = 500;
    }

    public Integer getCode() {
        return code;
    }
}
