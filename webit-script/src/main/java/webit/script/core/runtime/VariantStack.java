// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.runtime;

import java.util.Map;
import webit.script.core.runtime.variant.VariantContext;
import webit.script.core.runtime.variant.VariantMap;
import webit.script.exceptions.ScriptRuntimeException;

/**
 *
 * @author Zqq
 */
public class VariantStack {

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
        this(Math.max(initialCapacity, contexts != null ? contexts.length : 0));
        //push(context);
        if (contexts != null) {
            System.arraycopy(contexts, 0, this.contexts, 0, contexts.length);
            current = contexts.length - 1;
        }
    }

    protected final void push(VariantContext context) {
        ensureCapacity(current + 2);
        contexts[++current] = context;
    }

    public final void push(VariantMap varMap) {
        push(new VariantContext(varMap));
    }

    public final VariantContext pop() {
        if (current < 0) {
            throw new ScriptRuntimeException("VariantContext stack overflow");
        }
        VariantContext element = contexts[current];
        contexts[current--] = null;
        return element;
    }

    public final int setToCurrentContext(final Map<String, Object> map) {
        int count = 0;
        if (map != null) {
            final VariantContext element = getCurrentContext();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                if (element.set(entry.getKey(), entry.getValue())) {
                    ++count;
                }
            }
        }
        return count;
    }

    public final int set(final int[] indexs, final Object[] values) {
        final VariantContext context = getCurrentContext();
        if (indexs != null && values != null) {
            int len = values.length;
            if (len > indexs.length) {
                len = indexs.length;
            }
            for (int i = 0; i < len; i++) {
                context.set(indexs[i], values[i]);
            }
            return len;
        }
        return 0;
    }

    public final boolean set(int upstairs, int index, Object value) {
        getContext(upstairs).set(index, value);
        return true;
    }

    public final boolean set(int offset, String key, Object value) {
        for (int i = current - offset; i >= 0; i--) {
            if (contexts[i].set(key, value)) {
                return true;
            }
        }
        return false;
    }

    public final boolean set(String key, Object value) {
        for (int i = current; i >= 0; i--) {
            if (contexts[i].set(key, value)) {
                return true;
            }
        }
        return false;
    }

    public final Object get(int upstairs, int index) {
        return getContext(upstairs).get(index);
    }

    public final Object[] get(final String[] keys) {
        final Object[] results = new Object[keys.length];
        for (int i = 0; i < results.length; i++) {
            results[i] = get(keys[i], true);
        }
        return results;
    }

    public final Object get(String key) {
        return get(key, true);
    }

    public final Object get(String key, boolean force) {
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

    public final int getCurrentIndex() {
        return current;
    }

    public final VariantContext getCurrentContext() {
        return contexts[current];
    }

    public final VariantContext getContext(int offset) {
        final int realIndex = current - offset;
        if (realIndex < 0 || realIndex > current) {
            throw new IndexOutOfBoundsException();
        }
        VariantContext element = (VariantContext) contexts[realIndex];
        return element;
    }

    private void ensureCapacity(int mincap) {
        if (mincap > contexts.length) {
            int newcap = ((contexts.length * 3) >> 1) + 1;
            Object[] olddata = contexts;
            contexts = new VariantContext[newcap < mincap ? mincap : newcap];
            System.arraycopy(olddata, 0, contexts, 0, current + 1);
        }
    }
}
