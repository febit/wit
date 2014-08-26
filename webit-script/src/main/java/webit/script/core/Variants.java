// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core;

import java.util.Map;
import webit.script.lang.KeyValueAccepter;

/**
 *
 * @author Zqq
 */
public final class Variants implements KeyValueAccepter {

    public final Object[] values;
    public final VariantIndexer varIndexer;

    public Variants(final VariantIndexer varIndexer) {
        this.values = new Object[(this.varIndexer = varIndexer).size];
    }

    @SuppressWarnings("unchecked")
    public void exportTo(final Map map) {
        final Object[] vars = this.values;
        final VariantIndexer indexer = this.varIndexer;
        for (int i = 0, len = vars.length; i < len; i++) {
            map.put(indexer.getName(i), vars[i]);
        }
    }

    public Object get(int index) {
        return values[index];
    }

    public Object get(String key) {
        int index;
        if ((index = this.varIndexer.getIndex(key)) >= 0) {
            return this.values[index];
        }
        return null;
    }

    public void set(int index, Object value) {
        values[index] = value;
    }

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

    public void merge(Variants src) {
        if (src == null) {
            return;
        }
        final String[] srcNames = src.varIndexer.getNames();
        final Object[] srcValues = src.values;
        final VariantIndexer destIndexer = this.varIndexer;
        final Object[] destValues = this.values;
        int index;
        for (int i = 0, len = srcNames.length; i < len; i++) {
            if ((index = destIndexer.getIndex(srcNames[i])) >= 0) {
                destValues[index] = srcValues[i];
            }
        }
    }

    public void set(String key, Object value) {
        int index;
        if ((index = this.varIndexer.getIndex(key)) >= 0) {
            this.values[index] = value;
        }
    }
}
