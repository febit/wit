// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.runtime.variant;

import java.util.Map;
import webit.script.exceptions.ScriptRuntimeException;

/**
 *
 * @author Zqq
 */
public final class VariantStack {

    private static final int initialCapacity = 10;
    //
    protected VariantContext[] contexts;
    //
    protected int current;

    public VariantStack(int initialCapacity) {
        contexts = new VariantContext[initialCapacity];
        current = -1;
    }

    public VariantStack() {
        this(initialCapacity);
    }

    public VariantStack(final VariantContext[] contexts) {
        this(initialCapacity > contexts.length ? initialCapacity : contexts.length + 3);
        System.arraycopy(contexts, 0, this.contexts, 0, contexts.length);
        current = contexts.length - 1;
    }

    public void push(final VariantMap varMap) {
        final int i = ++current;
        if (i >= contexts.length) {
            ensureCapacity(i);
        }
        contexts[i] = new VariantContext(varMap);
    }

    public VariantContext pop() {
        if (current < 0) {
            throw new ScriptRuntimeException("VariantContext stack overflow");
        }
        VariantContext element = contexts[current];
        contexts[current--] = null;
        return element;
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
        for (int i = current; i >= 0; i++) {
            context = contexts[i];
            int index = context.getIndex(key);
            if (index >= 0) {
                return context.get(index);
            }
        }
        if (force) {
            throw new ScriptRuntimeException("Not found key named:" + key);
        }
        return null;
    }

    public int getCurrentIndex() {
        return current;
    }

    public VariantContext getCurrentContext() {
        return contexts[current];
    }

    public VariantContext getContext(int offset) {
        final int realIndex = current - offset;
        if (realIndex < 0 || realIndex > current) {
            throw new IndexOutOfBoundsException();
        }
        return contexts[realIndex];
    }

    private void ensureCapacity(int maxIndex) {
        maxIndex++;
        if (maxIndex > contexts.length) {
            int newcap = ((contexts.length * 3) >> 1) + 1;
            Object[] olddata = contexts;
            contexts = new VariantContext[newcap < maxIndex ? maxIndex : newcap];
            System.arraycopy(olddata, 0, contexts, 0, current + 1);
        }
    }
}
