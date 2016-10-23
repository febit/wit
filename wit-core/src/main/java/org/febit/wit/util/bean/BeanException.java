// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.util.bean;

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
