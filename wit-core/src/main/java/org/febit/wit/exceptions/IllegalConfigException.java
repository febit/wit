package org.febit.wit.exceptions;

/**
 *
 * @author zqq90
 */
public class IllegalConfigException extends RuntimeException {

    public IllegalConfigException() {
    }

    public IllegalConfigException(String message) {
        super(message);
    }

    public IllegalConfigException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalConfigException(Throwable cause) {
        super(cause);
    }

    public IllegalConfigException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
