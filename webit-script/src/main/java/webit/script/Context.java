// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script;

import java.util.HashMap;
import java.util.Map;
import webit.script.core.VariantIndexer;
import webit.script.core.ast.loop.LoopCtrl;
import webit.script.core.runtime.VariantContext;
import webit.script.core.runtime.VariantStack;
import webit.script.exceptions.NotFunctionException;
import webit.script.io.Out;
import webit.script.method.MethodDeclare;
import webit.script.resolvers.ResolverManager;
import webit.script.util.ScriptVoid;
import webit.script.util.Stack;
import webit.script.util.keyvalues.KeyValues;
import webit.script.util.keyvalues.KeyValuesUtil;

/**
 *
 * @author Zqq
 */
public final class Context {

    public static final ScriptVoid VOID = new ScriptVoid();

    private Out out;
    private Stack<Out> outStack;
    private Map<Object, Object> localMap;
    public final Context topContext;
    public final KeyValues rootValues;
    public final String encoding;
    public final LoopCtrl loopCtrl;
    public final VariantStack vars;
    public final Template template;
    public final ResolverManager resolverManager;
    public final boolean isByteStream;

    public Context(final Template template, final Out out, final KeyValues rootValues) {
        this.template = template;
        this.out = out;
        this.topContext = this;
        this.rootValues = rootValues;
        this.encoding = out.getEncoding();
        this.isByteStream = out.isByteStream();
        this.resolverManager = template.engine.getResolverManager();
        this.loopCtrl = new LoopCtrl();
        this.vars = new VariantStack();
    }

    public Context(final Context parent, final Template template, final KeyValues params) {
        this.template = template;
        this.out = parent.out;
        this.topContext = parent.topContext;
        this.rootValues = template.engine.isShareRootData()
                ? KeyValuesUtil.wrap(parent.rootValues, params)
                : params;
        this.encoding = parent.encoding;
        this.isByteStream = parent.isByteStream;
        this.resolverManager = parent.resolverManager;
        this.loopCtrl = new LoopCtrl();
        this.vars = new VariantStack();
    }

    public Context(final Context parent, final Template template, final VariantContext[] parentVarContexts, final boolean containsRootContext) {
        this.template = template;
        this.out = parent.out;
        this.topContext = parent.topContext;
        this.rootValues = parent.rootValues;
        this.encoding = parent.encoding;
        this.isByteStream = parent.isByteStream;
        this.resolverManager = parent.resolverManager;
        this.loopCtrl = new LoopCtrl();
        this.vars = new VariantStack(parentVarContexts, containsRootContext);
    }

    public void pushRootVars(final VariantIndexer varIndexer) {
        this.vars.pushRootVars(varIndexer, this.rootValues);
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

    /**
     * Export a named var.
     *
     * @since 1.5.0
     * @param name
     * @return
     */
    public Object export(String name) {
        VariantContext variantContext = this.vars.getCurrentContext();
        if (variantContext != null) {
            return variantContext.get(name);
        }
        return null;
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
        Object func = export(name);
        if (func != null && func instanceof MethodDeclare) {
            return new Function(this.template, (MethodDeclare) func, this.encoding, this.out.isByteStream());
        }
        throw new NotFunctionException(func);
    }

    /**
     * Export vars to a given map.
     *
     * @param map
     */
    public void exportTo(final Map map) {
        VariantContext variantContext = this.vars.getCurrentContext();
        if (variantContext != null) {
            variantContext.exportTo(map);
        }
    }
}
