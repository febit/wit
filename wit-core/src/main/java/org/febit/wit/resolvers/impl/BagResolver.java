// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.resolvers.impl;

import org.febit.wit.lang.Bag;
import org.febit.wit.resolvers.GetResolver;
import org.febit.wit.resolvers.SetResolver;

/**
 *
 * @author zqq90
 */
public class BagResolver implements GetResolver, SetResolver {

    @Override
    public Object get(Object object, Object property) {
        return ((Bag) object).get(property);
    }

    @Override
    public void set(Object object, Object property, Object value) {
        ((Bag) object).set(property, value);
    }

    @Override
    public Class getMatchClass() {
        return Bag.class;
    }
}
