// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.util.collection;

/**
 *
 * @author Zqq
 */
public class ShortArrayIterAdapter extends AbstractIter<Short> {

    private final short [] array;
    private final int max;

    public ShortArrayIterAdapter(short[] array) {
        this.array = array;
        this.max = array.length - 1;
    }

    protected Short _next() {
        return array[_index];
    }

    public boolean hasNext() {
        return _index < max;
    }
}
