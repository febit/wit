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
    //NOTE: the first keeps null, real contexts start from index=1;
    private VariantContext[] contexts;
    //
    private int current;
    private VariantContext currentContext;

    public VariantStack() {
        contexts = new VariantContext[initialCapacity];
        current = 0;
        currentContext = null;
    }

    public VariantStack(final VariantContext[] contexts) {
        final int len;
        System.arraycopy(contexts, 0,
                this.contexts = new VariantContext[initialCapacity > (len = contexts.length) ? initialCapacity : len + 3],
                1, //NOTE: skip top
                len);
        currentContext = contexts[(current = len) - 1];
    }

    public void push(final VariantMap varMap) {
        final int i;
        VariantContext[] cnts;
        if ((i = ++this.current) == (cnts = this.contexts).length) {
            System.arraycopy(cnts, 0,
                    cnts = this.contexts = new VariantContext[i << 1],
                    0, i);
        }
        cnts[i] = this.currentContext = varMap._size > 0 ? new VariantContext(varMap) : null;
    }

    public void pop() {
        VariantContext[] _contexts;
        (_contexts = this.contexts)[current--] = null;
        currentContext = _contexts[current];
    }

    public void setToCurrentContext(final Map<String, Object> map) {
        final VariantContext element;
        if (map != null && (element = currentContext) != null) {
            element.set(map);
        }
    }

    public void setArgumentsForFunction(final int argsIndex, final int[] indexs, final Object[] values) {
        final Object[] contextValues;
        (contextValues = currentContext.values)[argsIndex] = values;
        if (indexs != null && values != null) {
            int i;
            if ((i = values.length) > indexs.length) {
                i = indexs.length;
            }
            while (i != 0) {
                --i;
                contextValues[indexs[i]] = values[i];
            }
        }
    }

    public void set(int index, Object value) {
        currentContext.values[index] = value;
    }

    public void resetCurrent() {
        final VariantContext context;
        if ((context = currentContext) != null) {
            final Object[] contextValues;
            int i = (contextValues = context.values).length;
            while (i != 0) {
                --i;
                contextValues[i] = null;
            }
        }
    }

    public void resetCurrentWith(int index, Object value, int index2, Object value2) {
        final Object[] contextValues;
        int i = (contextValues = currentContext.values).length;
        do {
            --i;
            contextValues[i] = null;
        } while (i != 0);
        contextValues[index] = value;
        contextValues[index2] = value2;
    }

    public void resetCurrentWith(int index, Object value, int index2, Object value2, int index3, Object value3) {
        final Object[] contextValues;
        int i = (contextValues = currentContext.values).length;
        do {
            --i;
            contextValues[i] = null;
        } while (i != 0);
        contextValues[index] = value;
        contextValues[index2] = value2;
        contextValues[index3] = value3;
    }

    public void set(int upstairs, int index, Object value) {
        contexts[current - upstairs].values[index] = value;
    }

//    public boolean set(String key, Object value) {
//        VariantContext context;
//        int i = current;
//        while (i > 0) {//NOTE: skip top
//            if ((context = contexts[i--]) != null && context.set(key, value)) {
//                return true;
//            }
//        }
//        return false;
//    }
    public Object get(int index) {
        return currentContext.values[index];
    }

    public Object get(int upstairs, int index) {
        return contexts[current - upstairs].values[index];
    }

    public Object[] get(final String[] keys) {
        int i;
        final Object[] results = new Object[i = keys.length];
        while (i != 0) {
            --i;
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
        int i = current;
        while (i > 0) {//NOTE: skip top
            if ((context = contexts[i--]) != null && (index = context.getIndex(key)) >= 0) {
                return context.get(index);
            }
        }
        if (force) {
            throw new ScriptRuntimeException("Not found key named:".concat(key));
        }
        return null;
    }

    public VariantContext getCurrentContext() {
        return currentContext;
    }

    public VariantContext getContext(int offset) {
        final int realIndex;
        if (offset >= 0 && (realIndex = current - offset) > 0) {//NOTE: skip top
            return contexts[realIndex];
        } else {
            throw new IndexOutOfBoundsException();
        }
    }
}
