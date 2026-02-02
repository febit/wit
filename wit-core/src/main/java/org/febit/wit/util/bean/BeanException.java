// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.util.bean;

import org.febit.wit.util.StringUtils;
import org.jspecify.annotations.Nullable;

public class BeanException extends RuntimeException {

    public BeanException(String message, @Nullable Object... args) {
        super(StringUtils.format(message, args));
    }

    public BeanException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
