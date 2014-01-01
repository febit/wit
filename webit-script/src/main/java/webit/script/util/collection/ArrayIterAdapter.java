// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.util.collection;

/**
 *
 * @author Zqq
 */
public class ArrayIterAdapter<E> extends AbstractIter<E> {

    private final E[] array;
    private final int max;

    public ArrayIterAdapter(E[] array) {
        this.array = array;
        this.max = array.length - 1;
    }

    @Override
    protected E _next() {
        return array[_index];
    }

    public boolean hasNext() {
        return _index < max;
    }
}
