// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script;

import java.util.Map;
import webit.script.core.VariantIndexer;
import webit.script.core.ast.loop.LoopCtrl;
import webit.script.core.runtime.VariantContext;
import webit.script.core.runtime.VariantStack;
import webit.script.io.Out;
import webit.script.resolvers.ResolverManager;
import webit.script.util.collection.ArrayStack;
import webit.script.util.collection.Stack;
import webit.script.util.keyvalues.KeyValues;

/**
 *
 * @author Zqq
 */
public final class Context {

    public static final Object VOID = new Object();
    //
    private Out out;
    private Stack<Out> outStack;
    public final KeyValues rootValues;
    public final String encoding;
    //
    public final LoopCtrl loopCtrl;
    public final VariantStack vars;
    public final Template template;
    public final ResolverManager resolverManager;
    public final boolean isByteStream;

    public Context(final Template template, final Out out, final KeyValues rootValues) {
        this.template = template;
        this.out = out;
        this.rootValues = rootValues;
        this.encoding = out.getEncoding();
        this.isByteStream = out.isByteStream();
        this.resolverManager = template.engine.getResolverManager();
        this.loopCtrl = new LoopCtrl();
        this.vars = new VariantStack();
    }

    public Context(final Context parent, final Template template, final VariantContext[] parentVarContexts) {
        this.template = template;
        this.out = parent.out;
        this.rootValues = parent.rootValues;
        this.encoding = parent.encoding;
        this.isByteStream = parent.isByteStream;
        this.resolverManager = parent.resolverManager;
        this.loopCtrl = new LoopCtrl();
        this.vars = new VariantStack(parent.vars, parentVarContexts);
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
        if (outStack == null) {
            outStack = new ArrayStack<Out>(5);
        }
        outStack.push(this.out);
        this.out = out;
    }

    /**
     * Dangerous !
     *
     * @deprecated
     */
    public void popOut() {
        //checkOutStack();
        this.out = outStack.pop();
    }

    public Out getOut() {
        return out;
    }

    public void out(final byte[] bytes) {
        if (bytes != null) {
            out.write(bytes);
        }
    }

    public void out(final char[] chars) {
        if (chars != null) {
            out.write(chars);
        }
    }

    public void out(final String string) {
        if (string != null) {
            out.write(string);
        }
    }

    public void out(final Object object) {
        if (object != null) {
            if (object.getClass() == String.class) {
                //if (object instanceof String) {
                out.write((String) object);
                return;
            } else {
                resolverManager.render(out, object);
                return;
            }
        }
    }

    public void exportTo(final Map map) {
        vars.getCurrentContext().exportTo(map);
    }
}
