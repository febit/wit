// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.lang.iter;

/**
 *
 * @author Zqq
 */
public class ArrayIterAdapter extends AbstractIter {

    private final Object[] array;
    private final int max;

    public ArrayIterAdapter(Object[] array) {
        this.array = array;
        this.max = array.length - 1;
    }

    @Override
    protected Object _next() {
        return array[_index];
    }

    public boolean hasNext() {
        return _index < max;
    }
}
