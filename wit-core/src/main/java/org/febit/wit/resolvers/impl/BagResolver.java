// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.resolvers.impl;

import org.febit.wit.lang.Bag;
import org.febit.wit.resolvers.GetResolver;
import org.febit.wit.resolvers.SetResolver;

/**
 * @author zqq90
 */
public class BagResolver implements GetResolver<Bag>, SetResolver<Bag> {

    @Override
    public Object get(Bag bag, Object property) {
        return bag.get(property);
    }

    @Override
    public void set(Bag bag, Object property, Object value) {
        bag.set(property, value);
    }

    @Override
    public Class<Bag> getMatchClass() {
        return Bag.class;
    }
}
