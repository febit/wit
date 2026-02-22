// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.exception;

import org.febit.wit.util.ClassUtils;
import org.jspecify.annotations.Nullable;

public class NoSuchFunctionException extends RuntimeException {

    public NoSuchFunctionException(@Nullable Object obj) {
        super("No such function: " + ClassUtils.name(obj));
    }
}
