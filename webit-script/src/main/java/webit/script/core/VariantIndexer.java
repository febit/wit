// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Zqq
 */
public class VariantIndexer {

    public static final VariantIndexer EMPTY = new VariantIndexer(new String[0]);

    public final int size;
    final String[] names;

    VariantIndexer(String[] names) {
        this.names = names;
        this.size = names.length;
    }

    VariantIndexer(Map<String, Integer> map) {
        final String[] myNames;
        names = myNames = new String[size = map.size()];
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            myNames[entry.getValue()] = entry.getKey();
        }
    }

    public int getIndex(final String name) {
        int i;
        final String[] _names;
        i = (_names = this.names).length;
        while (i != 0) {
            --i;
            if (_names[i].equals(name)) {
                return i;
            }
        }
        return -1;
    }

    public final String[] getNames() {
        return names;
    }

    public final String getName(int index) {
        return this.names[index];
    }

    private static final int MAX_OF_NORMAL_VAR_MAP = 6;

    public static VariantIndexer getVariantIndexer(final Map<String, Integer> map) {
        if (map == null || map.isEmpty()) {
            return VariantIndexer.EMPTY;
        } else if (map.size() <= VariantIndexer.MAX_OF_NORMAL_VAR_MAP) {
            return new VariantIndexer(map);
        } else {
            return new HashVariantIndexer(map);
        }
    }

    private static final class HashVariantIndexer extends VariantIndexer {

        private final Map<String, Integer> varIndexMap;

        HashVariantIndexer(Map<String, Integer> map) {
            super(new String[map.size()]);
            final Map<String, Integer> newMap;
            //resize map
            this.varIndexMap = newMap = new HashMap<String, Integer>((map.size() + 1) * 4 / 3, 0.75f);
            String name;
            Integer index;
            final String[] myNames = this.names;
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                myNames[(index = entry.getValue())] = (name = entry.getKey());
                newMap.put(name, index);
            }
        }

        @Override
        public int getIndex(final String name) {
            Integer index;
            return (index = varIndexMap.get(name)) != null ? index : -1;
        }
    }
}
