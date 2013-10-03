// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.runtime.variant;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Zqq
 */
public final class HashVariantMap extends VariantMap {

    private final Map<String, Integer> varIndexMap;

    public HashVariantMap(Map<String, Integer> map) {
        super(new String[map.size()]);
        final Map<String, Integer> newMap;
        //resize map
        this.varIndexMap = newMap = new HashMap<String, Integer>((map.size() + 1) * 4 / 3, 0.75f);
        String name;
        Integer index;
        final String[] myNames = this.names;
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            name = entry.getKey();
            index = entry.getValue();
            myNames[index] = name;
            newMap.put(name, index);
        }
    }

    @Override
    public int getIndex(final String name) {
        Integer index;
        return (index = varIndexMap.get(name)) != null ? index : -1;
    }
}
