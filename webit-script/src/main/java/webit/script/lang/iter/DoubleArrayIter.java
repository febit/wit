// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.lang.iter;

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
        return array[++_index];
    }
}
