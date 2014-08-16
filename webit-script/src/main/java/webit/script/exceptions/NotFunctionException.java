// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.exceptions;

/**
 *
 * @author zqq
 */
public class NotFunctionException extends RuntimeException {

    public NotFunctionException(String message) {
        super(message);
    }

    public NotFunctionException(Object real) {
        this("Not a function but a [" + (real == null ? "null" : real.getClass().getName())+ "].");
    }
}
