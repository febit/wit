// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.util.iter;

/**
 *
 * @author Zqq
 */
public class ByteArrayIterAdapter extends AbstractIter {

    private final byte[] array;
    private final int max;

    public ByteArrayIterAdapter(byte[] array) {
        this.array = array;
        this.max = array.length - 1;
    }

    protected Byte _next() {
        return array[_index];
    }

    public boolean hasNext() {
        return _index < max;
    }
}
