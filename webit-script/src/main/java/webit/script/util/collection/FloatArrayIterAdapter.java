// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.util.collection;

/**
 *
 * @author Zqq
 */
public class FloatArrayIterAdapter extends AbstractIter<Float> {

    private final float [] array;
    private final int max;

    public FloatArrayIterAdapter(float[] array) {
        this.array = array;
        this.max = array.length - 1;
    }

    protected Float _next() {
        return array[_index];
    }

    public boolean hasNext() {
        return _index < max;
    }
}
