// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.runtime.variant;

import java.util.Map;
import webit.script.exceptions.ScriptRuntimeException;

/**
 *
 * @author Zqq
 */
public final class VariantStack {

    private static final int initialCapacity = 16;
    //
    private VariantContext[] contexts;
    //
    private int current;

    public VariantStack() {
        contexts = new VariantContext[initialCapacity];
        current = -1;
    }

    public VariantStack(final VariantContext[] contexts) {
        final int len;
        System.arraycopy(contexts, 0,
                this.contexts = new VariantContext[initialCapacity > (len = contexts.length) ? initialCapacity : len + 3],
                0, len);
        current = len - 1;
    }

    public void push(final VariantMap varMap) {
        final int i;
        VariantContext[] cnts;
        if ((i = ++current) >= (cnts = this.contexts).length) {
            System.arraycopy(cnts, 0,
                    cnts = contexts = new VariantContext[i << 1],
                    0, current + 1);
        }
        cnts[i] = new VariantContext(varMap);
    }

    public void pop() {
        contexts[current--] = null;
    }

    public int setToCurrentContext(final Map<String, Object> map) {
        int count = 0;
        if (map != null) {
            final VariantContext element = contexts[current]; //getCurrentContext();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                if (element.set(entry.getKey(), entry.getValue())) {
                    ++count;
                }
            }
        }
        return count;
    }

    public int set(final int[] indexs, final Object[] values) {
        if (indexs != null && values != null) {
            int len = values.length;
            if (len > indexs.length) {
                len = indexs.length;
            }
            //final VariantContext context = contexts[current]; //getCurrentContext();
            final Object[] contextValues = contexts[current].values;
            for (int i = 0; i < len; i++) {
                //context.set(indexs[i], values[i]);
                contextValues[indexs[i]] = values[i];
            }
            return len;
        }
        return 0;
    }

    public void set(int index, Object value) {
        //getContext(upstairs).set(index, value);
        contexts[current].values[index] = value;
    }

    public void resetCurrent() {
        final Object[] contextValues;
        int i = (contextValues = contexts[current].values).length - 1;
        while (i >= 0) {
            contextValues[i--] = null;
        }
    }

    public void resetCurrentWith(int index, Object value, int index2, Object value2) {
        final Object[] contextValues;
        int i = (contextValues = contexts[current].values).length - 1;
        while (i >= 0) {
            contextValues[i--] = null;
        }
        contextValues[index] = value;
        contextValues[index2] = value2;
    }

    public void resetCurrentWith(int index, Object value, int index2, Object value2, int index3, Object value3) {
        final Object[] contextValues;
        int i = (contextValues = contexts[current].values).length - 1;
        while (i >= 0) {
            contextValues[i--] = null;
        }
        contextValues[index] = value;
        contextValues[index2] = value2;
        contextValues[index3] = value3;
    }

    public void set(int upstairs, int index, Object value) {
        //getContext(upstairs).set(index, value);
        contexts[current - upstairs].values[index] = value;
    }

    public boolean set(int offset, String key, Object value) {
        for (int i = current - offset; i >= 0; i--) {
            if (contexts[i].set(key, value)) {
                return true;
            }
        }
        return false;
    }

    public boolean set(String key, Object value) {
        for (int i = current; i >= 0; i--) {
            if (contexts[i].set(key, value)) {
                return true;
            }
        }
        return false;
    }

    public Object get(int upstairs, int index) {
        //return getContext(upstairs).get(index);
        return contexts[current - upstairs].values[index];
    }

    public Object[] get(final String[] keys) {
        final Object[] results = new Object[keys.length];
        for (int i = 0; i < results.length; i++) {
            results[i] = get(keys[i], true);
        }
        return results;
    }

    public Object get(String key) {
        return get(key, true);
    }

    public Object get(String key, boolean force) {
        VariantContext context;
        int index;
        for (int i = current; i >= 0; i++) {
            if ((index = (context = contexts[i]).getIndex(key)) >= 0) {
                return context.get(index);
            }
        }
        if (force) {
            throw new ScriptRuntimeException("Not found key named:" + key);
        }
        return null;
    }

    public VariantContext getCurrentContext() {
        return contexts[current];
    }

    public VariantContext getContext(int offset) {
        final int realIndex;
        if ((realIndex = current - offset) < 0 || realIndex > current) {
            throw new IndexOutOfBoundsException();
        }
        return contexts[realIndex];
    }
}
