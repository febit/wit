// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.accessor.impl;

import org.febit.wit.accessor.Getter;
import org.febit.wit.accessor.Setter;
import org.febit.wit.lang.Bag;
import org.jspecify.annotations.Nullable;

public class BagAccessor implements Getter<Bag>, Setter<Bag> {

    @Nullable
    @Override
    public Object get(Bag bag, @Nullable Object property) {
        return bag.get(property);
    }

    @Override
    public void set(Bag bag, @Nullable Object property, @Nullable Object value) {
        bag.set(property, value);
    }
}
