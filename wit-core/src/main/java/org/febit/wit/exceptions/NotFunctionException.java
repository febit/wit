// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.exceptions;

import org.febit.wit.util.ClassUtil;

/**
 *
 * @author zqq90
 */
public class NotFunctionException extends RuntimeException {

    public NotFunctionException(Object obj) {
        super("Not function: ".concat(ClassUtil.getClassName(obj)));
    }
}
