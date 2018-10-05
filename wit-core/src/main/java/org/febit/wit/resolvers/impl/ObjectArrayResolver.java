// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.resolvers.impl;

import org.febit.wit.exceptions.ScriptRuntimeException;
import org.febit.wit.resolvers.GetResolver;
import org.febit.wit.resolvers.SetResolver;
import org.febit.wit.util.StringUtil;

/**
 * @author zqq90
 */
public class ObjectArrayResolver implements GetResolver<Object[]>, SetResolver<Object[]> {

    @Override
    public Object get(Object[] array, Object property) {
        if (property instanceof Number) {
            return array[((Number) property).intValue()];
        }
        switch (property.toString()) {
            case "size":
            case "length":
                return array.length;
            case "isEmpty":
                return array.length == 0;
            default:
        }
        throw new ScriptRuntimeException(StringUtil.format("Invalid property: array#{}", property));
    }

    @Override
    public void set(Object[] array, Object property, Object value) {
        if (property instanceof Number) {
            try {
                array[((Number) property).intValue()] = value;
                return;
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new ScriptRuntimeException(StringUtil.format(
                        "Array index out of bounds, index={}", property), e);
            }
        }
        throw new ScriptRuntimeException(StringUtil.format("Invalid property: array#{}", property));
    }

    @Override
    public Class<Object[]> getMatchClass() {
        return Object[].class;
    }
}
