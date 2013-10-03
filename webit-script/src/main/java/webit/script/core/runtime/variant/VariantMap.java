// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.runtime.variant;

import java.util.Map;

/**
 *
 * @author Zqq
 */
public class VariantMap {
    
    public final static VariantMap EMPTY = new VariantMap(new String[0]);

    protected final String[] names;
    protected final int _size;

    VariantMap(String[] names) {
        this.names = names;
        this._size = names != null ? names.length : 0;
    }

    public VariantMap(Map<String, Integer> map) {

        final String[] myNames;
        names = myNames = new String[_size = map.size()];
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            myNames[entry.getValue()] = entry.getKey();
        }
    }

    public int getIndex(final String name) {
        for (int i = 0; i < _size; i++) {
            if (names[i].equals(name)) {
                return i;
            }
        }
        return -1;
    }

    public final String getName(int index) {
        return names[index];
    }

    public final int size() {
        return _size;
    }
}
