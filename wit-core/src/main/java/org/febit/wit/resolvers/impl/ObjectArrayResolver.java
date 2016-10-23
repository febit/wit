// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.resolvers.impl;

import org.febit.wit.exceptions.ScriptRuntimeException;
import org.febit.wit.resolvers.GetResolver;
import org.febit.wit.resolvers.SetResolver;
import org.febit.wit.util.StringUtil;

/**
 *
 * @author zqq90
 */
public class ObjectArrayResolver implements GetResolver, SetResolver {

    @Override
    public Object get(Object object, Object property) {
        if (property instanceof Number) {
            return ((Object[]) object)[((Number) property).intValue()];
        }
        if ("length".equals(property) || "size".equals(property)) {
            return ((Object[]) object).length;
        }
        if ("isEmpty".equals(property)) {
            return ((Object[]) object).length == 0;
        }
        throw new ScriptRuntimeException(StringUtil.format("Invalid property: array#{}", property));
    }

    @Override
    public void set(Object object, Object property, Object value) {
        if (property instanceof Number) {
            try {
                ((Object[]) object)[((Number) property).intValue()] = value;
                return;
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new ScriptRuntimeException(StringUtil.format("Array index out of bounds, index={}", property));
            }
        }
        throw new ScriptRuntimeException(StringUtil.format("Invalid property: array#{}", property));
    }

    @Override
    public Class getMatchClass() {
        return Object[].class;
    }
}
