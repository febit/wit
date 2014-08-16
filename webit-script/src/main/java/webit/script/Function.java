// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script;

import java.io.OutputStream;
import java.io.Writer;
import webit.script.io.Out;
import webit.script.io.impl.DiscardOut;
import webit.script.io.impl.OutputStreamOut;
import webit.script.io.impl.WriterOut;
import webit.script.method.MethodDeclare;
import webit.script.util.keyvalues.KeyValuesUtil;

/**
 * Exported function.
 * 
 * @since 1.5.0
 * @author zqq
 */
public class Function {

    protected final Template container;
    protected final MethodDeclare methodDeclare;
    protected final Out defaultOut;

    public Function(Template container, MethodDeclare methodDeclare) {
        this(container, methodDeclare, container.engine.getEncoding(), false);
    }

    public Function(Template container, MethodDeclare methodDeclare, String encoding, boolean isByteStream) {
        this(container, methodDeclare, new DiscardOut(encoding, isByteStream));
    }

    public Function(Template container, MethodDeclare methodDeclare, Out defaultOut) {
        this.methodDeclare = methodDeclare;
        this.container = container;
        this.defaultOut = defaultOut;
    }

    protected Context createContext(Out out) {
        return new Context(this.container, out, KeyValuesUtil.EMPTY_KEY_VALUES);
    }

    protected Context createContext() {
        return createContext(defaultOut);
    }

    protected Object _invoke(Context context, Object... args) {
        return this.methodDeclare.invoke(context, args);
    }

    public Object invoke(Object... args) {
        return _invoke(createContext(), args);
    }

    public Object invokeWithOut(Out out, Object... args) {
        return _invoke(createContext(out), args);
    }

    public Object invokeWithOut(Writer writer, Object... args) {
        return invokeWithOut(new WriterOut(writer, container.engine), args);
    }

    public Object invokeWithOut(final OutputStream outputStream, Object... args) {
        return invokeWithOut(new OutputStreamOut(outputStream, container.engine), args);
    }

    public Object invokeWithOut(final String encoding, final OutputStream outputStream, Object... args) {
        return invokeWithOut(new OutputStreamOut(outputStream, encoding, container.engine), args);
    }
}
