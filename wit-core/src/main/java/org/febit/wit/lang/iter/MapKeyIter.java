// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.iter;

import java.util.Iterator;
import java.util.Map;
import org.febit.wit.lang.KeyIter;

/**
 *
 * @author zqq90
 */
public final class MapKeyIter extends AbstractIter implements KeyIter {

    private final Iterator<Map.Entry> iterator;
    private Map.Entry current;

    @SuppressWarnings("unchecked")
    public MapKeyIter(Map map) {
        this.iterator = map.entrySet().iterator();
    }

    @Override
    public Object value() {
        return this.current.getValue();
    }

    @Override
    protected Object _next() {
        return (this.current = iterator.next()).getKey();
    }

    @Override
    public boolean hasNext() {
        return this.iterator.hasNext();
    }
}
