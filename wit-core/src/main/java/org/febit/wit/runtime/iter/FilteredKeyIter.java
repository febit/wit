// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.iter;

import java.util.function.BiPredicate;

public class FilteredKeyIter<I extends KeyIter>
        extends FilteredIter<I>
        implements KeyIter {

    protected FilteredKeyIter(I iter, BiPredicate<I, Object> filter) {
        super(iter, filter);
    }

    @Override
    public Object value() {
        return iter.value();
    }
}
