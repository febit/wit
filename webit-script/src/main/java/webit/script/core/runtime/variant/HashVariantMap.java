// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.runtime.variant;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Zqq
 */
public final class HashVariantMap extends VariantMap {

    private final Map<String, Integer> varMap;

    public HashVariantMap(Map<String, Integer> map) {
        super(new String[map.size()]);
        varMap = new HashMap<String, Integer>((map.size() + 1) * 4 / 3, 0.75f);
        String name;
        Integer index;
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            name = entry.getKey();
            index = entry.getValue();
            array[index] = name;
            varMap.put(name, index);
        }
    }

    @Override
    public int getIndex(final String name) {
        Integer index;
        return (index = varMap.get(name)) != null ? index : -1;
    }
}
