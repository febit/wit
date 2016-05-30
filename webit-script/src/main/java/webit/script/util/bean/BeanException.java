// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.util.bean;

/**
 *
 * @author zqq90
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
