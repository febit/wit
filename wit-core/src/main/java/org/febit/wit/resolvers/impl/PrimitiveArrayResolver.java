// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.resolvers.impl;

import org.febit.wit.exceptions.ScriptRuntimeException;
import org.febit.wit.resolvers.GetResolver;
import org.febit.wit.resolvers.RegistModeResolver;
import org.febit.wit.resolvers.ResolverManager;
import org.febit.wit.resolvers.SetResolver;
import org.febit.wit.util.ALU;
import org.febit.wit.util.ArrayUtil;
import org.febit.wit.util.StringUtil;

/**
 *
 * @author zqq90
 */
public class PrimitiveArrayResolver implements RegistModeResolver, GetResolver, SetResolver {

    @Override
    public Object get(Object object, Object property) {
        if (property instanceof Number) {
            return ArrayUtil.get(object, ((Number) property).intValue());
        }
        switch (property.toString()) {
            case "size":
            case "length":
                return ArrayUtil.getSize(object);
            case "isEmpty":
                return ArrayUtil.getSize(object) == 0;
        }
        throw new ScriptRuntimeException(StringUtil.format("Invalid property: array#{}", property));
    }

    @Override
    public void set(Object o1, Object property, Object value) {
        if (property instanceof Number) {
            int index = ((Number) property).intValue();
            final Class cls = o1.getClass();
            try {
                if (cls == int[].class) {
                    ((int[]) o1)[index] = ((Number) value).intValue();
                    return;
                }
                if (cls == char[].class) {
                    ((char[]) o1)[index] = (Character) value;
                    return;
                }
                if (cls == long[].class) {
                    ((long[]) o1)[index] = ((Number) value).longValue();
                    return;
                }
                if (cls == short[].class) {
                    ((short[]) o1)[index] = ((Number) value).shortValue();
                    return;
                }
                if (cls == double[].class) {
                    ((double[]) o1)[index] = ((Number) value).doubleValue();
                    return;
                }
                if (cls == boolean[].class) {
                    ((boolean[]) o1)[index] = ALU.isTrue(value);
                    return;
                }
                if (cls == float[].class) {
                    ((float[]) o1)[index] = ((Number) value).floatValue();
                    return;
                }
                if (cls == byte[].class) {
                    ((byte[]) o1)[index] = ((Number) value).byteValue();
                    return;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new ScriptRuntimeException(StringUtil.format("Array index out of bounds, index={}", index), e);
            } catch (ClassCastException e) {
                throw new ScriptRuntimeException(e.getMessage(), e);
            }
        }
        throw new ScriptRuntimeException(StringUtil.format("Invalid property or can't write: array#{}", property));
    }

    @Override
    public Class getMatchClass() {
        return null;
    }

    @Override
    public void regist(ResolverManager resolverManager) {
        resolverManager.registResolver(int[].class, this);
        resolverManager.registResolver(boolean[].class, this);
        resolverManager.registResolver(char[].class, this);
        resolverManager.registResolver(float[].class, this);
        resolverManager.registResolver(double[].class, this);
        resolverManager.registResolver(long[].class, this);
        resolverManager.registResolver(short[].class, this);
        resolverManager.registResolver(byte[].class, this);
    }
}
