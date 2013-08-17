// Copyright (c) 2013, Webit Team. All Rights Reserved.

package webit.script.core.runtime.variant;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Zqq
 */
public class HashVariantMap implements VariantMap {

    private final String[] array;
    private final Map<String, Integer> varMap;

    public HashVariantMap(Map<String, Integer> map) {
        int size = map.size();
        array = new String[size];
        varMap = new HashMap((size + 1) * 4 / 3, 0.75f);

        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            String name = entry.getKey();
            Integer index = entry.getValue();

            array[index] = name;
            varMap.put(name, index);
        }
    }

    public int getIndex(String name) {
        Integer index = varMap.get(name);
        return index != null ? index : -1;
    }

    public String getName(int index) {
        return array[index];
    }

    public int size() {
        return array.length;
    }
}
