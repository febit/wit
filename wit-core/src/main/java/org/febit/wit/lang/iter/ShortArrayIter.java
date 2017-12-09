// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.lang.iter;

/**
 *
 * @author zqq90
 */
public final class ShortArrayIter extends AbstractArrayIter {

    private final short[] array;

    public ShortArrayIter(short[] array) {
        super(array.length - 1);
        this.array = array;
    }

    @Override
    public Object next() {
        return array[++cursor];
    }
}
