// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.exceptions;

import webit.script.util.StringUtil;

/**
 *
 * @author zqq
 */
public class NotFunctionException extends RuntimeException {

    public NotFunctionException(Object real) {
        super(StringUtil.concat("Not a function but a [", real == null ? "null" : real.getClass().getName(), "]."));
    }
}
