// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.resolvers.impl;

import java.util.Map;
import webit.script.resolvers.GetResolver;
import webit.script.resolvers.SetResolver;

/**
 *
 * @author zqq90
 */
public class MapResolver implements GetResolver, SetResolver {

    @Override
    public Class getMatchClass() {
        return Map.class;
    }

    @Override
    public Object get(Object object, Object property) {
        return ((Map) object).get(property);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void set(Object object, Object property, Object value) {
        ((Map) object).put(property, value);
    }
}
