// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.runtime;

import java.util.Map;
import webit.script.core.VariantIndexer;
import webit.script.exceptions.ScriptRuntimeException;
import webit.script.util.collection.Iter;
import webit.script.util.keyvalues.KeyValues;

/**
 *
 * @author Zqq
 */
public final class VariantStack {

    private static final int DEFAULT_CAPACITY = 12;
    //NOTE: the first keeps null, real contexts start from index=1;
    private VariantContext[] contexts;
    //
    private Object[] rootContextValues;
    private int current;
    private VariantContext currentContext;

    public VariantStack() {
        this.contexts = new VariantContext[DEFAULT_CAPACITY];
        this.current = 0;
        //currentContext = null;
    }

    public VariantStack(final VariantContext[] contexts, final boolean containsRootContext) {
        if (contexts != null) {
            final int len;
            System.arraycopy(contexts, 0,
                    this.contexts = new VariantContext[DEFAULT_CAPACITY > (len = contexts.length) ? DEFAULT_CAPACITY : len + 3],
                    1, //NOTE: skip top
                    len);
            this.currentContext = contexts[(this.current = len) - 1];
            this.rootContextValues = containsRootContext ? contexts[0].values : null;
        } else {
            this.contexts = new VariantContext[DEFAULT_CAPACITY];
            this.current = 0;
            this.rootContextValues = null;
        }
        //this.rootContextValues = parent.rootContextValues;
    }

    public void pushRootVars(final VariantIndexer varIndexer, final KeyValues rootValues) {
        push(varIndexer);
        final VariantContext rootContext;
        if ((rootContext = this.currentContext) != null) {
            this.rootContextValues = rootContext.values;
            rootValues.exportTo(rootContext);
        }
    }

    public void push(final VariantIndexer varIndexer) {
        final int i;
        VariantContext[] cnts;
        if ((i = ++this.current) == (cnts = this.contexts).length) {
            System.arraycopy(cnts, 0,
                    cnts = this.contexts = new VariantContext[i << 1],
                    0, i);
        }
        cnts[i] = this.currentContext = varIndexer.size > 0 ? new VariantContext(varIndexer) : null;
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

    public void mergeCurrentContext(final VariantStack fromStack) {
        final VariantContext from = fromStack.currentContext;
        final VariantContext to = this.currentContext;
        if (from != null && to != null) {
            final String[] fromNames = from.varIndexer.getNames();
            final Object[] fromValues = from.values;
            final VariantIndexer toIndexer = to.varIndexer;
            final Object[] toValues = to.values;
            int index;
            for (int i = 0, size = fromNames.length; i < size; i++) {
                if ((index = toIndexer.getIndex(fromNames[i])) >= 0) {
                    toValues[index] = fromValues[i];
                }
            }
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

    public void resetForForIn(Object item) {
        final Object[] contextValues;
        (contextValues = currentContext.values)[1] = item;
        int i = contextValues.length - 1;
        while (i != 1) {
            contextValues[i] = null;
            --i;
        }
    }

    public void resetForForMap(Object key, Object value) {
        final Object[] contextValues;
        (contextValues = currentContext.values)[1] = key;
        contextValues[2] = value;
        int i = contextValues.length - 1;
        while (i != 2) {
            contextValues[i] = null;
            --i;
        }
    }

    public void setToRoot(int index, Object value) {
        this.rootContextValues[index] = value;
    }

    public Object getFromRoot(int index) {
        return this.rootContextValues[index];
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
            if ((context = contexts[i--]) != null && (index = context.varIndexer.getIndex(key)) >= 0) {
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

    public int getCurrentDepth() {
        return current - 1;
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
