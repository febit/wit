// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.util.collection;

/**
 *
 * @author Zqq
 */
public class BooleanArrayIterAdapter extends AbstractIter<Boolean> {

    private final boolean [] array;
    private final int max;

    public BooleanArrayIterAdapter(boolean[] array) {
        this.array = array;
        this.max = array.length - 1;
    }

    protected Boolean _next() {
        return array[_index];
    }

    public boolean hasNext() {
        return _index < max;
    }
}
