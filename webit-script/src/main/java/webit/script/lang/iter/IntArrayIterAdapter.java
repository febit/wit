// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.lang.iter;

/**
 *
 * @author Zqq
 */
public class IntArrayIterAdapter extends AbstractIter {

    private final int[] array;
    private final int max;

    public IntArrayIterAdapter(int[] array) {
        this.array = array;
        this.max = array.length - 1;
    }

    protected Integer _next() {
        return array[_index];
    }

    public boolean hasNext() {
        return _index < max;
    }
}
