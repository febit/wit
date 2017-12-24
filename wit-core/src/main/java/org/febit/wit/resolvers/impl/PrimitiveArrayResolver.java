// Copyright (c) 2013-present, febit.org. All Rights Reserved.
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
    public Object get(Object array, Object property) {
        if (property instanceof Number) {
            return ArrayUtil.get(array, ((Number) property).intValue());
        }
        switch (property.toString()) {
            case "size":
            case "length":
                return ArrayUtil.getSize(array);
            case "isEmpty":
                return ArrayUtil.getSize(array) == 0;
            default:
        }
        throw new ScriptRuntimeException(StringUtil.format("Invalid property: array#{}", property));
    }

    @Override
    public void set(Object array, Object property, Object value) {
        if (property instanceof Number) {
            int index = ((Number) property).intValue();
            final Class cls = array.getClass();
            try {
                if (cls == int[].class) {
                    ((int[]) array)[index] = ((Number) value).intValue();
                    return;
                }
                if (cls == char[].class) {
                    ((char[]) array)[index] = (Character) value;
                    return;
                }
                if (cls == long[].class) {
                    ((long[]) array)[index] = ((Number) value).longValue();
                    return;
                }
                if (cls == short[].class) {
                    ((short[]) array)[index] = ((Number) value).shortValue();
                    return;
                }
                if (cls == double[].class) {
                    ((double[]) array)[index] = ((Number) value).doubleValue();
                    return;
                }
                if (cls == boolean[].class) {
                    ((boolean[]) array)[index] = ALU.isTrue(value);
                    return;
                }
                if (cls == float[].class) {
                    ((float[]) array)[index] = ((Number) value).floatValue();
                    return;
                }
                if (cls == byte[].class) {
                    ((byte[]) array)[index] = ((Number) value).byteValue();
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
