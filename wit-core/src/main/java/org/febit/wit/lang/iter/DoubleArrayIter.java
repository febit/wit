// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.lang.iter;

/**
 *
 * @author zqq90
 */
public final class DoubleArrayIter extends AbstractArrayIter {

    private final double[] array;

    public DoubleArrayIter(double[] array) {
        super(array.length - 1);
        this.array = array;
    }

    @Override
    public Object next() {
        return array[++cursor];
    }
}
