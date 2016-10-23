// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.exceptions;

import org.febit.wit.util.StringUtil;

/**
 *
 * @author zqq90
 */
public class NotFunctionException extends RuntimeException {

    public NotFunctionException(Object real) {
        super(StringUtil.format("Not a function but a [{}].", real == null ? "null" : real.getClass()));
    }
}
