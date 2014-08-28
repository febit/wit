// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.resolvers.impl;

import java.util.Collection;
import java.util.List;
import webit.script.exceptions.ScriptRuntimeException;
import webit.script.resolvers.GetResolver;
import webit.script.resolvers.SetResolver;
import webit.script.util.StringUtil;

/**
 *
 * @author Zqq
 */
public class ListResolver implements GetResolver, SetResolver {

    public Class<?> getMatchClass() {
        return Collection.class;
    }

    public Object get(Object object, Object property) {
        if (property instanceof Number) {
            try {
                return ((List) object).get(((Number) property).intValue());
            } catch (IndexOutOfBoundsException e) {
                throw new ScriptRuntimeException(StringUtil.concat("index out of bounds:", property));
            }
        } else {
            if ("size".equals(property)) {
                return ((List) object).size();
            } else if ("isEmpty".equals(property)) {
                return ((List) object).isEmpty();
            }
            throw new ScriptRuntimeException(StringUtil.concat("Invalid property or can't read: java.util.List#", property));
        }
    }

    @SuppressWarnings("unchecked")
    public void set(Object object, Object property, Object value) {
        if (property instanceof Number) {
            final int index;
            final int size;
            final List list;
            if ((index = ((Number) property).intValue())
                    >= (size = (list = (List) object).size())) {
                for (int i = index - size; i != 0; i--) {
                    list.add(null);
                }
                list.add(value);
            } else {
                list.set(index, value);
            }
        } else {
            throw new ScriptRuntimeException(StringUtil.concat("Invalid property or can't write: java.util.List#", property));
        }
    }
}
