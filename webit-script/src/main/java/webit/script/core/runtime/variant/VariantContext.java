// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.runtime.variant;

import java.util.Map;

/**
 *
 * @author Zqq
 */
public final class VariantContext {

    //private static final Object[] EMPTY_ARRAY = new Object[0];
    final Object[] values;
    private final VariantMap varMap;

    public VariantContext(final VariantMap varMap) {
        //this.varMap = varMap;
        //int size;
        //this.values = (size = varMap.size()) != 0 ? new Object[size] : EMPTY_ARRAY;
        this.values = new Object[(this.varMap = varMap).size()];
    }

    @SuppressWarnings("unchecked")
    public void exportTo(final Map map) {
        final Object[] vars;
        final String[] keys = varMap.names;
        for (int i = 0, len = (vars = this.values).length; i < len; i++) {
            map.put(keys[i], vars[i]);
        }
    }

    public Object get(int index) {
        return values[index];
    }

    public void set(int index, Object value) {
        values[index] = value;
    }

    public boolean set(String key, Object value) {
        int index;
        if ((index = varMap.getIndex(key)) >= 0) {
            values[index] = value;
            return true;
        }
        return false;
    }

    public int getIndex(String key) {
        return varMap.getIndex(key);
    }

    public boolean hasKey(String key) {
        return varMap.getIndex(key) >= 0;
    }

    public int size() {
        return values.length;
    }
}
