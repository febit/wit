// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core.runtime;

import java.util.Map;
import webit.script.core.VariantIndexer;
import webit.script.util.keyvalues.KeyValueAccepter;

/**
 *
 * @author Zqq
 */
public final class VariantContext implements KeyValueAccepter {

    final Object[] values;
    final VariantIndexer varIndexer;

    public VariantContext(final VariantIndexer varIndexer) {
        this.values = new Object[(this.varIndexer = varIndexer).size];
    }

    @SuppressWarnings("unchecked")
    public void exportTo(final Map map) {
        final Object[] vars;
        final VariantIndexer indexer = this.varIndexer;
        for (int i = 0, len = (vars = this.values).length; i < len; i++) {
            map.put(indexer.getName(i), vars[i]);
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
//        if ((index = this.varIndexer.getIndex(key)) >= 0) {
//            values[index] = value;
//            return true;
//        }
//        return false;
//    }
    public void set(final Map<String, Object> map) {
        int index;
        final VariantIndexer _varMap = this.varIndexer;
        final Object[] _values = this.values;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if ((index = _varMap.getIndex(entry.getKey())) >= 0) {
                _values[index] = entry.getValue();
            }
        }
    }

//    public int getIndex(String key) {
//        return this.varIndexer.getIndex(key);
//    }
//
//    public boolean hasKey(String key) {
//        return this.varIndexer.getIndex(key) >= 0;
//    }

    public int size() {
        return values.length;
    }

    public void set(String key, Object value) {
        int index;
        if ((index = this.varIndexer.getIndex(key)) >= 0) {
            this.values[index] = value;
        }
    }
}
