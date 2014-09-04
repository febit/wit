// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.util.bean;

/**
 *
 * @author Zqq
 */
public class BeanUtilException extends RuntimeException {

    public BeanUtilException(String message) {
        super(message);
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}
