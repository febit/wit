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
        if (property instanceof Number) {
            try {
                return ((List) object).get(((Number) property).intValue());
            } catch (IndexOutOfBoundsException e) {
                throw new ScriptRuntimeException(StringUtil.format("index out of bounds:{}", property));
            }
        }
        if ("size".equals(property)) {
            return ((List) object).size();
        }
        if ("isEmpty".equals(property)) {
            return ((List) object).isEmpty();
        }
        throw new ScriptRuntimeException(StringUtil.format("Invalid property or can't read: java.util.List#{}", property));
    }

    @SuppressWarnings("unchecked")
    @Override
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
                return;
            } else {
                list.set(index, value);
                return;
            }
        }
        throw new ScriptRuntimeException(StringUtil.format("Invalid property or can't write: java.util.List#{}", property));
    }
}
