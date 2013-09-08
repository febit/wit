// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.runtime.variant;

import java.util.Map;

/**
 *
 * @author Zqq
 */
public final class ArrayVariantMap implements VariantMap {

    private final String[] array;

    public ArrayVariantMap(Map<String, Integer> map) {
        int size = map.size();
        array = new String[size];
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            String name = entry.getKey();
            Integer index = entry.getValue();
            array[index] = name;
        }
    }

    public ArrayVariantMap(String[] array) {
        this.array = array;
    }

    public int getIndex(final String name) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(name)) {
                return i;
            }
        }
        return -1;
    }

    public String getName(int index) {
        return array[index];
    }

    public int size() {
        return array.length;
    }
}
