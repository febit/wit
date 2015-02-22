// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.util.bean;

/**
 *
 * @author Zqq
 */
public class BeanException extends RuntimeException {

    public BeanException(String message) {
        super(message);
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}
