// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.lang.iter;

import java.util.Enumeration;

/**
 *
 * @author zqq90
 */
public final class EnumerationIter extends AbstractIter {

    private final Enumeration enumeration;

    public EnumerationIter(Enumeration enumeration) {
        this.enumeration = enumeration;
    }

    @Override
    protected Object _next() {
        return enumeration.nextElement();
    }

    @Override
    public boolean hasNext() {
        return enumeration.hasMoreElements();
    }
}
