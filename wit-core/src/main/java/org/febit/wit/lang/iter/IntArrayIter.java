// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.lang.iter;

/**
 *
 * @author zqq90
 */
public final class IntArrayIter extends AbstractArrayIter {

    private final int[] array;

    public IntArrayIter(int[] array) {
        super(array.length - 1);
        this.array = array;
    }

    @Override
    public Object next() {
        return array[++cursor];
    }
}
