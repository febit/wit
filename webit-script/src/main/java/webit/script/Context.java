// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script;

import webit.script.core.Variants;
import java.util.HashMap;
import java.util.Map;
import webit.script.core.VariantIndexer;
import webit.script.core.ast.loop.LoopCtrl;
import webit.script.exceptions.NotFunctionException;
import webit.script.exceptions.ScriptRuntimeException;
import webit.script.io.Out;
import webit.script.lang.KeyValues;
import webit.script.lang.MethodDeclare;
import webit.script.lang.Void;
import webit.script.resolvers.ResolverManager;
import webit.script.util.KeyValuesUtil;
import webit.script.util.Stack;

/**
 *
 * @author Zqq
 */
public final class Context {

    public static final Void VOID = new Void();

    private static final int DEFAULT_CAPACITY = 12;

    //NOTE: the first keeps null, real contexts start from index=1;
    private Variants[] varses;
    private Object[] rootVars;
    private int current;
    private Variants currentVars;

    private Out out;
    private Stack<Out> outStack;
    private Map<Object, Object> localMap;
    public final Context topContext;
    public final KeyValues rootParams;
    public final String encoding;
    public final LoopCtrl loopCtrl;
    public final Template template;
    public final ResolverManager resolverManager;
    public final boolean isByteStream;

    public Context(final Template template, final Out out, final KeyValues rootParams) {
        this.template = template;
        this.out = out;
        this.topContext = this;
        this.rootParams = rootParams;
        this.encoding = out.getEncoding();
        this.isByteStream = out.isByteStream();
        this.resolverManager = template.engine.getResolverManager();
        this.loopCtrl = new LoopCtrl();

        this.varses = new Variants[DEFAULT_CAPACITY];
        this.current = 0;
    }

    public Context(final Context parent, final Template template, final KeyValues params) {
        this.template = template;
        this.out = parent.out;
        this.topContext = parent.topContext;
        this.rootParams = template.engine.isShareRootData()
                ? KeyValuesUtil.wrap(parent.rootParams, params)
                : params;
        this.encoding = parent.encoding;
        this.isByteStream = parent.isByteStream;
        this.resolverManager = parent.resolverManager;
        this.loopCtrl = new LoopCtrl();

        this.varses = new Variants[DEFAULT_CAPACITY];
        this.current = 0;
    }

    public Context(final Context parent, final Template template, final Variants[] parentVarses, final boolean withRootVars) {
        this.template = template;
        this.out = parent.out;
        this.topContext = parent.topContext;
        this.rootParams = parent.rootParams;
        this.encoding = parent.encoding;
        this.isByteStream = parent.isByteStream;
        this.resolverManager = parent.resolverManager;
        this.loopCtrl = new LoopCtrl();

        if (parentVarses != null) {
            final int len;
            System.arraycopy(parentVarses, 0,
                    this.varses = new Variants[DEFAULT_CAPACITY > (len = parentVarses.length) ? DEFAULT_CAPACITY : len + 3],
                    1, //NOTE: skip top
                    len);
            this.currentVars = parentVarses[(this.current = len) - 1];
            this.rootVars = withRootVars ? parentVarses[0].values : null;
        } else {
            this.varses = new Variants[DEFAULT_CAPACITY];
            this.current = 0;
            this.rootVars = null;
        }
    }

    public void pushWithRootParams(final VariantIndexer varIndexer) {
        push(varIndexer);
        final Variants rootContext;
        if ((rootContext = this.currentVars) != null) {
            this.rootVars = rootContext.values;
            rootParams.exportTo(rootContext);
        }
    }

    /**
     * Dangerous !
     *
     * @param out
     * @deprecated
     */
    public void pushOut(Out out) {
        Stack<Out> stack;
        if ((stack = this.outStack) == null) {
            stack = this.outStack = new Stack<Out>(5);
        }
        stack.push(this.out);
        this.out = out;
    }

    /**
     * Dangerous !
     *
     * @deprecated
     */
    public void popOut() {
        this.out = this.outStack.pop();
    }

    public Out getOut() {
        return this.out;
    }

    public void out(final byte[] bytes) {
        if (bytes != null) {
            this.out.write(bytes);
        }
    }

    public void out(final char[] chars) {
        if (chars != null) {
            this.out.write(chars);
        }
    }

    public void out(final String string) {
        if (string != null) {
            this.out.write(string);
        }
    }

    public void out(final Object object) {
        if (object != null) {
            if (object.getClass() == String.class) {
                this.out.write((String) object);
            } else {
                this.resolverManager.render(this.out, object);
            }
        }
    }

    public Object getLocalVar(final Object key) {
        final Map<Object, Object> map;
        return (map = this.localMap) != null ? map.get(key) : null;
    }

    public void setLocalVar(final Object key, final Object value) {
        final Map<Object, Object> map;
        if ((map = this.localMap) != null) {
            map.put(key, value);
        } else {
            (this.localMap = new HashMap<Object, Object>()).put(key, value);
        }
    }


    public void push(final VariantIndexer varIndexer) {
        final int i;
        Variants[] varses;
        if ((i = ++this.current) == (varses = this.varses).length) {
            System.arraycopy(varses, 0,
                    varses = this.varses = new Variants[i << 1],
                    0, i);
        }
        varses[i] = this.currentVars = varIndexer.size > 0 ? new Variants(varIndexer) : null;
    }

    public void pop() {
        Variants[] varses;
        (varses = this.varses)[current--] = null;
        this.currentVars = varses[current];
    }

    public void setArgumentsForFunction(final int argsCount, final Object[] args) {
        final int len;
        final Object[] values;
        if (((values = currentVars.values)[0] = args) != null
                && argsCount != 0
                && (len = args.length) != 0) {
            System.arraycopy(args, 0, values, 1, argsCount > len ? len : argsCount);
        }
    }

    public void set(int index, Object value) {
        currentVars.values[index] = value;
    }

    public void resetCurrentVars() {
        final Variants vars;
        if ((vars = currentVars) != null) {
            final Object[] values;
            int i = (values = vars.values).length;
            while (i != 0) {
                --i;
                values[i] = null;
            }
        }
    }

    public void resetForForIn(Object item) {
        final Object[] values;
        (values = currentVars.values)[1] = item;
        int i = values.length - 1;
        while (i != 1) {
            values[i] = null;
            --i;
        }
    }

    public void resetForForMap(Object key, Object value) {
        final Object[] values;
        (values = currentVars.values)[1] = key;
        values[2] = value;
        int i = values.length - 1;
        while (i != 2) {
            values[i] = null;
            --i;
        }
    }

    public void setToRoot(int index, Object value) {
        this.rootVars[index] = value;
    }

    public Object getFromRoot(int index) {
        return this.rootVars[index];
    }

    public void set(int upstairs, int index, Object value) {
        varses[current - upstairs].values[index] = value;
    }

    public Object get(int index) {
        return currentVars.values[index];
    }

    public Object get(int upstairs, int index) {
        return varses[current - upstairs].values[index];
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
        Variants vars;
        int index;
        int i = current;
        while (i > 0) {//NOTE: skip top
            if ((vars = varses[i--]) != null && (index = vars.varIndexer.getIndex(key)) >= 0) {
                return vars.values[index];
            }
        }
        if (force) {
            throw new ScriptRuntimeException("Not found variant named:".concat(key));
        }
        return null;
    }

    public Variants getCurrentVars() {
        return currentVars;
    }

    public int getCurrentVarsDepth() {
        return current - 1;
    }

    public Variants getVars(int offset) {
        final int realIndex;
        if (offset >= 0 && (realIndex = current - offset) > 0) {//NOTE: skip top
            return varses[realIndex];
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    /**
     * Export a named function.
     *
     * @since 1.5.0
     * @param name
     * @return
     * @throws NotFunctionException
     */
    public Function exportFunction(String name) throws NotFunctionException {
        Object func = get(name, false);
        if (func instanceof MethodDeclare) {
            return new Function(this.template, (MethodDeclare) func, this.encoding, this.isByteStream);
        }
        throw new NotFunctionException(func);
    }

    /**
     * Export vars to a given map.
     *
     * @param map
     */
    public void exportTo(final Map map) {
        Variants vars = getCurrentVars();
        if (vars != null) {
            vars.exportTo(map);
        }
    }
}
