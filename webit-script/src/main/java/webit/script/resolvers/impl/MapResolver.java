// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.resolvers.impl;

import java.util.Map;
import webit.script.resolvers.GetResolver;
import webit.script.resolvers.MatchMode;
import webit.script.resolvers.SetResolver;

/**
 *
 * @author Zqq
 */
public class MapResolver implements GetResolver, SetResolver {

    public MatchMode getMatchMode() {
        return MatchMode.INSTANCEOF;
    }

    public Class<?> getMatchClass() {
        return Map.class;
    }

    public Object get(Object object, Object property) {
        return ((Map) object).get(property);
    }

    @SuppressWarnings("unchecked")
    public boolean set(Object object, Object property, Object value) {
        ((Map) object).put(property, value);
        return true;
    }
}
