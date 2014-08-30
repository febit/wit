// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.resolvers.impl;

import webit.script.exceptions.ScriptRuntimeException;
import webit.script.resolvers.GetResolver;
import webit.script.resolvers.SetResolver;
import webit.script.util.StringUtil;

/**
 *
 * @author zqq
 */
public class ObjectArrayResolver implements GetResolver, SetResolver {

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
        throw new ScriptRuntimeException(StringUtil.concat("Invalid property: array#", property));
    }

    public void set(Object object, Object property, Object value) {
        if (property instanceof Number) {
            try {
                ((Object[]) object)[((Number) property).intValue()] = value;
                return;
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new ScriptRuntimeException(StringUtil.concat("Array index out of bounds, index=", (Number) property));
            }
        }
        throw new ScriptRuntimeException(StringUtil.concat("Invalid property: array#", property));
    }

    public Class getMatchClass() {
        return Object[].class;
    }
}
