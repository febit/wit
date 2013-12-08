// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.runtime.variant;

import java.util.Map;

/**
 *
 * @author Zqq
 */
public final class VariantContext {

    final Object[] values;
    private final VariantMap varMap;

    public VariantContext(final VariantMap varMap) {
        this.values = new Object[(this.varMap = varMap).size()];
    }

    @SuppressWarnings("unchecked")
    public void exportTo(final Map map) {
        final Object[] vars;
        final String[] keys = this.varMap.names;
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

//    public boolean set(String key, Object value) {
//        int index;
//        if ((index = this.varMap.getIndex(key)) >= 0) {
//            values[index] = value;
//            return true;
//        }
//        return false;
//    }

    public void set(final Map<String, Object> map) {
        int index;
        final VariantMap _varMap = this.varMap;
        final Object[] _values = this.values;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if ((index = _varMap.getIndex(entry.getKey())) >= 0) {
                _values[index] = entry.getValue();
            }
        }
    }

    public int getIndex(String key) {
        return this.varMap.getIndex(key);
    }

    public boolean hasKey(String key) {
        return this.varMap.getIndex(key) >= 0;
    }

    public int size() {
        return values.length;
    }
}
