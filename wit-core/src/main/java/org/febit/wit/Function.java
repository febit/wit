// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit;

import org.febit.wit.io.Out;
import org.febit.wit.io.impl.DiscardOut;
import org.febit.wit.io.impl.OutputStreamOut;
import org.febit.wit.io.impl.WriterOut;
import org.febit.wit.lang.InternedEncoding;
import org.febit.wit.lang.MethodDeclare;
import org.febit.wit.lang.VariantIndexer;

import java.io.OutputStream;
import java.io.Writer;

/**
 * Exported function.
 *
 * @author zqq90
 */
@SuppressWarnings({
        "WeakerAccess"
})
public class Function {

    private static final VariantIndexer[] EMPTY_INDEXERS = {VariantIndexer.EMPTY};

    protected final Template template;
    protected final MethodDeclare methodDeclare;
    protected final Out defaultOut;

    public Function(Template container, MethodDeclare methodDeclare) {
        this(container, methodDeclare, DiscardOut.INSTANCE);
    }

    public Function(Template container, MethodDeclare methodDeclare, InternedEncoding encoding, boolean isByteStream) {
        this(container, methodDeclare, new DiscardOut(encoding, isByteStream));
    }

    public Function(Template template, MethodDeclare methodDeclare, Out defaultOut) {
        this.methodDeclare = methodDeclare;
        this.template = template;
        this.defaultOut = defaultOut;
    }

    protected InternalContext createContext(Out out) {
        return new InternalContext(template, out, Vars.EMPTY, EMPTY_INDEXERS, 0, null, null);
    }

    protected InternalContext createContext() {
        return createContext(defaultOut);
    }

    protected Object doInvoke(InternalContext context, Object... args) {
        return this.methodDeclare.invoke(context, args);
    }

    public Object invoke(Object... args) {
        return doInvoke(createContext(), args);
    }

    public Object invokeWithOut(Out out, Object... args) {
        return doInvoke(createContext(out), args);
    }

    public Object invokeWithOut(Writer writer, Object... args) {
        return invokeWithOut(new WriterOut(writer, template.getEngine()), args);
    }

    public Object invokeWithOut(final OutputStream out, Object... args) {
        return invokeWithOut(new OutputStreamOut(out, template.getEngine()), args);
    }

    public Object invokeWithOut(final String encoding, final OutputStream out, Object... args) {
        return invokeWithOut(InternedEncoding.intern(encoding), out, args);
    }

    public Object invokeWithOut(final InternedEncoding encoding, final OutputStream out, Object... args) {
        return invokeWithOut(new OutputStreamOut(out, encoding, template.getEngine()), args);
    }
}
