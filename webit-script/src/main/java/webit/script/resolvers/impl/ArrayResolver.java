// Copyright (c) 2013, Webit Team. All Rights Reserved.

package webit.script.resolvers.impl;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import webit.script.Configurable;
import webit.script.Engine;
import webit.script.exceptions.ScriptRuntimeException;
import webit.script.resolvers.GetResolver;
import webit.script.resolvers.MatchMode;
import webit.script.resolvers.RegistModeResolver;
import webit.script.resolvers.ResolverManager;
import webit.script.resolvers.SetResolver;
import webit.script.resolvers.ToStringResolver;
import webit.script.util.CollectionUtil;

/**
 *
 * @author Zqq
 */
public class ArrayResolver implements RegistModeResolver, GetResolver, SetResolver, ToStringResolver {

    public MatchMode getMatchMode() {
        return MatchMode.REGIST;
    }

    public Class<?> getMatchClass() {
        return Object[].class;
    }

    public Object get(Object object, Object property) {
        if (property instanceof Number) {
            return CollectionUtil.getByIndex(object, property);
        } else {
            if ("size".equals(property) || "length".equals(property)) {
                return CollectionUtil.getSize(object);
            } else if ("isEmpty".equals(property)) {
                return CollectionUtil.getSize(object) == 0;
            }
            throw new ScriptRuntimeException("Invalid property or can't read: array#" + property);
        }
    }

    public boolean set(Object object, Object property, Object value) {
        if (property instanceof Number) {
            CollectionUtil.setByIndex(object, property, value);
            return true;
        } else {
            throw new ScriptRuntimeException("Invalid property or can't write: array#" + property);
        }
    }

    public void regist(ResolverManager resolverManager) {
        resolverManager.registResolver(Object[].class, this, MatchMode.INSTANCEOF);
        resolverManager.registResolver(int[].class, this, MatchMode.EQUALS);
        resolverManager.registResolver(boolean[].class, this, MatchMode.EQUALS);
        resolverManager.registResolver(char[].class, this, MatchMode.EQUALS);
        resolverManager.registResolver(float[].class, this, MatchMode.EQUALS);
        resolverManager.registResolver(double[].class, this, MatchMode.EQUALS);
        resolverManager.registResolver(long[].class, this, MatchMode.EQUALS);
        resolverManager.registResolver(short[].class, this, MatchMode.EQUALS);
        resolverManager.registResolver(byte[].class, this, MatchMode.EQUALS);
    }

    public String toString(Object bean) {
        return CollectionUtil.arrayToString(bean);
    }
}
