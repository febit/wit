// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.exceptions;

import org.febit.wit.util.ClassUtils;
import org.jspecify.annotations.Nullable;

public class NotFunctionException extends RuntimeException {

    public NotFunctionException(@Nullable Object obj) {
        super("Not function: " + ClassUtils.name(obj));
    }
}
