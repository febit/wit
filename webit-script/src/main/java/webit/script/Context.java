package webit.script;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import webit.script.core.runtime.LoopCtrl;
import webit.script.core.runtime.VariantStack;
import webit.script.core.runtime.variant.VariantContext;
import webit.script.exceptions.ScriptRuntimeException;
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
    protected Stack<OutputStream> outStack;
    protected OutputStream out;
    public final String encoding;
    //
    public final LoopCtrl loopCtrl;
    public final VariantStack vars;
    public final Template template;
    public final ResolverManager resolverManager;

    // **********************
    public Context(Template template, OutputStream out) {

        this.template = template;
        this.out = out;
        this.encoding = template.engine.encoding;
        this.loopCtrl = new LoopCtrl();
        this.vars = new VariantStack();
        this.resolverManager = template.engine.getResolverManager();
    }

    public Context(Context parent, VariantContext[] parentVarContexts) {

        this.template = parent.template;
        this.out = out == null ? parent.out : out;
        this.encoding = parent.encoding;
        this.loopCtrl = new LoopCtrl();
        this.vars = new VariantStack(parentVarContexts);
        this.resolverManager = parent.resolverManager;
    }

//    public boolean setToRoot(String key, Object value) {
//        root.put(key, value);
//        return true;
//    }
    protected final void checkOutStack() {
        if (outStack == null) {
            outStack = new ArrayStack<OutputStream>(5);
        }
    }

    @Deprecated
    public final void pushOut(OutputStream out) {
        checkOutStack();
        outStack.push(this.out);
        this.out = out;
    }

    @Deprecated
    public final OutputStream popOut() {
        checkOutStack();
        OutputStream old = this.out;
        this.out = outStack.pop();
        return old;
    }

    public final OutputStream getOut() {
        return out;
    }

    public final void out(byte[] bytes) {
        if (bytes != null) {
            try {
                out.write(bytes);
            } catch (IOException ex) {
                throw new ScriptRuntimeException(ex);
            }
        }
    }

    public final void exportTo(Map map) {
        vars.getCurrentContext().exportTo(map);
    }

    public final void out(Object object) {
        if (object != null) {
            if (object instanceof byte[]) {
                out((byte[]) object);
            } else {
                try {
                    out(resolverManager.toBytes(object, encoding));
                } catch (UnsupportedEncodingException ex) {
                    throw new ScriptRuntimeException(ex);
                }
            }
        }
    }

    protected static void setContext() {
    }
}
