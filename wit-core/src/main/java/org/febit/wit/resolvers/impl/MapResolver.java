// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.resolvers.impl;

import java.util.Map;
import org.febit.wit.resolvers.GetResolver;
import org.febit.wit.resolvers.SetResolver;

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
