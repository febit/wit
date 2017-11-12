// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.resolvers.impl;

import org.febit.wit.exceptions.ScriptRuntimeException;
import org.febit.wit.resolvers.GetResolver;
import org.febit.wit.util.StringUtil;

/**
 *
 * @author zqq90
 */
public class CharSequenceResolver implements GetResolver {

    @Override
    public Object get(final Object object, final Object property) {
        if (property instanceof Number) {
            try {
                return ((CharSequence) object).charAt(((Number) property).intValue());
            } catch (IndexOutOfBoundsException e) {
                throw new ScriptRuntimeException(StringUtil.format("index out of bounds:{}", property), e);
            }
        }
        switch (property.toString()) {
            case "size":
            case "length":
                return ((CharSequence) object).length();
            case "isEmpty":
                return ((CharSequence) object).length() == 0;
            default:
        }
        throw new ScriptRuntimeException(StringUtil.format("Invalid property or can't read: java.lang.CharSequence#{}", property));
    }

    @Override
    public Class getMatchClass() {
        return CharSequence.class;
    }
}
