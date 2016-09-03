// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.resolvers.impl;

import java.util.Collection;
import java.util.List;
import webit.script.exceptions.ScriptRuntimeException;
import webit.script.resolvers.GetResolver;
import webit.script.resolvers.SetResolver;
import webit.script.util.StringUtil;

/**
 *
 * @author zqq90
 */
public class ListResolver implements GetResolver, SetResolver {

    @Override
    public Class getMatchClass() {
        return Collection.class;
    }

    @Override
    public Object get(Object object, Object property) {
        if (property instanceof Number && object instanceof List) {
            try {
                return ((List) object).get(((Number) property).intValue());
            } catch (IndexOutOfBoundsException e) {
                throw new ScriptRuntimeException(StringUtil.format("index out of bounds:{}", property));
            }
        }
        if ("size".equals(property)) {
            return ((Collection) object).size();
        }
        if ("isEmpty".equals(property)) {
            return ((Collection) object).isEmpty();
        }
        throw new ScriptRuntimeException(StringUtil.format("Invalid property or can't read: java.util.Collection#{}", property));
    }

    @SuppressWarnings("unchecked")
    @Override
    public void set(Object object, Object property, Object value) {
        if (property instanceof Number) {
            final Collection collection = (Collection) object;
            final int index = ((Number) property).intValue();
            final int size = collection.size();
            if (index >= size) {
                for (int i = index - size; i != 0; i--) {
                    collection.add(null);
                }
                collection.add(value);
                return;
            } else if (object instanceof List) {
                ((List) collection).set(index, value);
                return;
            }
        }
        throw new ScriptRuntimeException(StringUtil.format("Invalid property or can't write: java.util.Collection#{}", property));
    }
}
