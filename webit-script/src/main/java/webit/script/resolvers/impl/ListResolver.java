// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.resolvers.impl;

import java.util.Collection;
import java.util.List;
import webit.script.exceptions.ScriptRuntimeException;
import webit.script.resolvers.GetResolver;
import webit.script.resolvers.MatchMode;
import webit.script.resolvers.SetResolver;

/**
 *
 * @author Zqq
 */
public class ListResolver implements GetResolver, SetResolver {

    public MatchMode getMatchMode() {
        return MatchMode.INSTANCEOF;
    }

    public Class<?> getMatchClass() {
        return Collection.class;
    }

    public Object get(Object object, Object property) {
        List list = (List) object;
        if (property instanceof Number) {
            try {
                return list.get(((Number) property).intValue());
            } catch (IndexOutOfBoundsException e) {
                throw new ScriptRuntimeException("index out of bounds:" + property);
            }
        } else {
            if ("size".equals(property)) {
                return list.size();
            } else if ("isEmpty".equals(property)) {
                return list.isEmpty();
            }
            throw new ScriptRuntimeException("Invalid property or can't read: java.util.List#" + property);
        }
    }

    public boolean set(Object object, Object property, Object value) {
        List list = (List) object;
        if (property instanceof Number) {
            try {
                list.set(((Number) property).intValue(), value);
            } catch (IndexOutOfBoundsException e) {
                throw new ScriptRuntimeException("index out of bounds:" + property);
            }
            return true;
        } else {
            throw new ScriptRuntimeException("Invalid property or can't write: java.util.List#" + property);
        }
    }
}
