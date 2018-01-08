// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.util.bean;

import org.febit.wit.util.StringUtil;

/**
 *
 * @author zqq90
 */
public class BeanException extends RuntimeException {

    public BeanException(String message, Object... args) {
        super(StringUtil.format(message, args));
    }

    public BeanException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
