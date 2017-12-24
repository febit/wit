// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.resolvers.impl;

import java.util.Map;
import org.febit.wit.resolvers.GetResolver;
import org.febit.wit.resolvers.SetResolver;

/**
 *
 * @author zqq90
 */
public class MapResolver implements GetResolver<Map>, SetResolver<Map> {

    @Override
    public Class<Map> getMatchClass() {
        return Map.class;
    }

    @Override
    public Object get(Map map, Object property) {
        return map.get(property);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void set(Map map, Object property, Object value) {
        map.put(property, value);
    }
}
