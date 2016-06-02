// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script;

import java.io.OutputStream;
import java.io.Writer;
import webit.script.core.VariantIndexer;
import webit.script.io.Out;
import webit.script.io.impl.DiscardOut;
import webit.script.io.impl.OutputStreamOut;
import webit.script.io.impl.WriterOut;
import webit.script.lang.MethodDeclare;
import webit.script.util.KeyValuesUtil;

/**
 * Exported function.
 *
 * @author zqq90
 */
public class Function {

    protected static final VariantIndexer[] EMPTY_INDEXERS = new VariantIndexer[]{VariantIndexer.EMPTY};

    protected final Template template;
    protected final MethodDeclare methodDeclare;
    protected final Out defaultOut;

    public Function(Template container, MethodDeclare methodDeclare) {
        this(container, methodDeclare, container.engine.getEncoding(), false);
    }

    public Function(Template container, MethodDeclare methodDeclare, String encoding, boolean isByteStream) {
        this(container, methodDeclare, new DiscardOut(encoding, isByteStream));
    }

    public Function(Template template, MethodDeclare methodDeclare, Out defaultOut) {
        this.methodDeclare = methodDeclare;
        this.template = template;
        this.defaultOut = defaultOut;
    }

    protected InternalContext createContext(Out out) {
        return new InternalContext(template, out, KeyValuesUtil.EMPTY_KEY_VALUES, EMPTY_INDEXERS, 0, null);
    }

    protected InternalContext createContext() {
        return createContext(defaultOut);
    }

    protected Object _invoke(InternalContext context, Object... args) {
        return this.methodDeclare.invoke(context, args);
    }

    public Object invoke(Object... args) {
        return _invoke(createContext(), args);
    }

    public Object invokeWithOut(Out out, Object... args) {
        return _invoke(createContext(out), args);
    }

    public Object invokeWithOut(Writer writer, Object... args) {
        return invokeWithOut(new WriterOut(writer, template.engine), args);
    }

    public Object invokeWithOut(final OutputStream outputStream, Object... args) {
        return invokeWithOut(new OutputStreamOut(outputStream, template.engine), args);
    }

    public Object invokeWithOut(final String encoding, final OutputStream outputStream, Object... args) {
        return invokeWithOut(new OutputStreamOut(outputStream, encoding, template.engine), args);
    }
}
