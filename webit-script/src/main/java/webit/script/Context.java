package webit.script;

import java.util.Map;
import webit.script.core.runtime.LoopCtrl;
import webit.script.core.runtime.VariantStack;
import webit.script.core.runtime.variant.VariantContext;
import webit.script.io.Out;
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
    protected Out out;
    protected Stack<Out> outStack;
    public final String encoding;
    //
    public final LoopCtrl loopCtrl;
    public final VariantStack vars;
    public final Template template;
    public final ResolverManager resolverManager;
    public final boolean enableAsmNative;

    public Context(Template template, Out out) {

        this.template = template;
        this.out = out;
        this.encoding = out.getEncoding();
        this.loopCtrl = new LoopCtrl();
        this.vars = new VariantStack();
        this.resolverManager = template.engine.getResolverManager();
        this.enableAsmNative = template.engine.isEnableAsmNative();
    }

    public Context(Context parent, Template template, VariantContext[] parentVarContexts) {

        this.template = template;
        this.out = parent.out;
        this.encoding = parent.encoding;
        this.loopCtrl = new LoopCtrl();
        this.vars = new VariantStack(parentVarContexts);
        this.resolverManager = parent.resolverManager;
        this.enableAsmNative = parent.enableAsmNative;
    }

    protected final void checkOutStack() {
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
    @Deprecated
    public final void pushOut(Out out) {
        checkOutStack();
        outStack.push(this.out);
        this.out = out;
    }

    /**
     * Dangerous !
     *
     * @return
     * @deprecated
     */
    @Deprecated
    public final Out popOut() {
        checkOutStack();
        Out old = this.out;
        this.out = outStack.pop();
        return old;
    }

    public final Out getOut() {
        return out;
    }

    public final void out(byte[] bytes) {
        if (bytes != null) {
            out.write(bytes);
        }
    }

    public final void out(String string) {
        if (string != null) {
            out.write(string);
        }
    }

    public final void out(Object object) {
        if (object != null) {
            Class objClass = object.getClass();
            if (objClass == byte[].class) {
                out((byte[]) object);
            } else if (objClass == String.class) {
                out((String) object);
            } else {
                out(resolverManager.toString(object));
            }
        }
    }

    public final void exportTo(Map map) {
        vars.getCurrentContext().exportTo(map);
    }
}
