// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.resolvers.impl;

import webit.script.exceptions.ScriptRuntimeException;
import webit.script.io.Out;
import webit.script.resolvers.GetResolver;
import webit.script.resolvers.OutResolver;
import webit.script.resolvers.RegistModeResolver;
import webit.script.resolvers.ResolverManager;
import webit.script.resolvers.SetResolver;
import webit.script.util.ArrayUtil;
import webit.script.util.StringUtil;

/**
 *
 * @author Zqq
 */
public class ArrayResolver implements RegistModeResolver, GetResolver, SetResolver, OutResolver {

    public Class<?> getMatchClass() {
        return null;
    }

    public Object get(Object object, Object property) {
        if (property instanceof Number) {
            return ArrayUtil.getByIndex(object, ((Number) property).intValue());
        } else {
            if ("length".equals(property) || "size".equals(property)) {
                return ArrayUtil.getSize(object);
            } else if ("isEmpty".equals(property)) {
                return ArrayUtil.getSize(object) == 0;
            }
            throw new ScriptRuntimeException(StringUtil.concat("Invalid property or can't read: array#", property));
        }
    }

    public boolean set(Object object, Object property, Object value) {
        if (property instanceof Number) {
            ArrayUtil.setByIndex(object, ((Number) property).intValue(), value);
            return true;
        } else {
            throw new ScriptRuntimeException(StringUtil.concat("Invalid property or can't write: array#", property));
        }
    }

    public void regist(ResolverManager resolverManager) {
        resolverManager.registResolver(Object[].class, this);
        resolverManager.registResolver(int[].class, this);
        resolverManager.registResolver(boolean[].class, this);
        resolverManager.registResolver(char[].class, this);
        resolverManager.registResolver(float[].class, this);
        resolverManager.registResolver(double[].class, this);
        resolverManager.registResolver(long[].class, this);
        resolverManager.registResolver(short[].class, this);
        resolverManager.registResolver(byte[].class, this);
    }

    public void render(final Out out, Object bean) {
        final Class objClass;
        if ((objClass = bean.getClass()) == char[].class) {
            out.write((char[]) bean);
        } else if (objClass == byte[].class) {
            out.write((byte[]) bean);
        } else {
            out.write(ArrayUtil.arrayToString(bean));
        }
    }
}
