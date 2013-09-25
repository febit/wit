// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script;

import java.util.Map;
import webit.script.core.runtime.LoopCtrl;
import webit.script.core.runtime.variant.VariantStack;
import webit.script.core.runtime.variant.VariantContext;
import webit.script.io.Out;
import webit.script.loggers.Logger;
import webit.script.resolvers.ResolverManager;
import webit.script.util.ArrayStack;
import webit.script.util.Stack;

/**
 *
 * @author Zqq
 */
public final class Context {

    public static final Object VOID = new Object();
    public static final Object UNDEFINED = new Object();
    //
    private Out out;
    private Stack<Out> outStack;
    public final String encoding;
    //
    public final LoopCtrl loopCtrl;
    public final VariantStack vars;
    public final Template template;
    public final ResolverManager resolverManager;
    public final boolean enableAsmNative;
    public final Logger logger;
    public final boolean isByteStream;

    public Context(final Template template, final Out out) {

        this.template = template;
        this.out = out;
        this.encoding = out.getEncoding();
        this.loopCtrl = new LoopCtrl();
        this.vars = new VariantStack();
        this.isByteStream = out.isByteStream();
        final Engine engine = template.engine;
        this.resolverManager = engine.getResolverManager();
        this.enableAsmNative = engine.isEnableAsmNative();
        this.logger = engine.getLogger();
    }

    public Context(final Context parent, final Template template, final VariantContext[] parentVarContexts) {

        this.template = template;
        this.out = parent.out;
        this.encoding = parent.encoding;
        this.isByteStream = parent.isByteStream;
        this.loopCtrl = new LoopCtrl();
        this.vars = parentVarContexts != null ? new VariantStack(parentVarContexts) : new VariantStack();
        this.resolverManager = parent.resolverManager;
        this.enableAsmNative = parent.enableAsmNative;
        this.logger = parent.logger;
    }

    private void checkOutStack() {
        if (outStack == null) {
            outStack = new ArrayStack<Out>(5);
        }
    }

    /**
     * Dangerous !
     *
     * @param out
     * @deprecated
     */
    public void pushOut(Out out) {
        checkOutStack();
        outStack.push(this.out);
        this.out = out;
    }

    /**
     * Dangerous !
     *
     * @return current out
     * @deprecated
     */
    public Out popOut() {
        checkOutStack();
        Out old = this.out;
        this.out = outStack.pop();
        return old;
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
        if (object instanceof String) {
            out.write((String) object);
        } else {
            if (object != null) {
                resolverManager.render(out, object);
            }
        }
    }

    public void exportTo(final Map map) {
        vars.getCurrentContext().exportTo(map);
    }
}
