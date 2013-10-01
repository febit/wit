// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.runtime.variant;

import java.util.Map;

/**
 *
 * @author Zqq
 */
public class VariantMap {

    protected final String[] array;
    protected final int _size;

    VariantMap(String[] array) {
        this.array = array;
        this._size = array != null ? array.length : 0;
    }

    public VariantMap(Map<String, Integer> map) {

        array = new String[_size = map.size()];
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            array[entry.getValue()] = entry.getKey();
        }
    }

    public int getIndex(final String name) {
        for (int i = 0; i < _size; i++) {
            if (array[i].equals(name)) {
                return i;
            }
        }
        return -1;
    }

    public final String getName(int index) {
        return array[index];
    }

    public final int size() {
        return _size;
    }
}
