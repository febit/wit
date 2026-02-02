// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit;

import lombok.RequiredArgsConstructor;
import org.febit.wit.io.DiscardOut;
import org.febit.wit.io.OutputStreamOut;
import org.febit.wit.io.WriterOut;
import org.febit.wit.lang.FunctionDeclare;
import org.febit.wit.lang.Out;
import org.febit.wit.lang.VariantIndexer;
import org.jspecify.annotations.Nullable;

import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.Charset;

import static org.febit.wit.util.Defaults.nvl;

/**
 * Exported function.
 */
@RequiredArgsConstructor
@SuppressWarnings("UnusedReturnValue")
public final class Function {

    private static final VariantIndexer[] EMPTY_INDEXERS = {VariantIndexer.EMPTY};

    private final Template template;
    private final FunctionDeclare functionDeclare;
    private final Out defaultOut;

    public Function(Template container, FunctionDeclare functionDeclare) {
        this(container, functionDeclare, DiscardOut.INSTANCE);
    }

    public Function(
            Template container,
            FunctionDeclare functionDeclare,
            Charset charset,
            boolean isByteStream
    ) {
        this(container, functionDeclare, new DiscardOut(charset, isByteStream));
    }

    private InternalContext createContext(Out out) {
        return new InternalContext(template, out, Vars.empty(), EMPTY_INDEXERS, 0, null, null);
    }

    private InternalContext createContext() {
        return createContext(defaultOut);
    }

    @Nullable
    private Object doInvoke(
            InternalContext context,
            @Nullable Object @Nullable ... args
    ) {
        return this.functionDeclare.invoke(context, args);
    }

    @Nullable
    public Object invoke(@Nullable Object @Nullable ... args) {
        return doInvoke(createContext(), args);
    }

    @Nullable
    public Object invokeWithOut(Out out, @Nullable Object @Nullable ... args) {
        return doInvoke(createContext(out), args);
    }

    @Nullable
    public Object invokeWithOut(Writer writer, @Nullable Object @Nullable ... args) {
        var engine = template.engine();
        return invokeWithOut(new WriterOut(writer, engine.charset(), engine.codecFactory()), args);
    }

    @Nullable
    public Object invokeWithOut(
            OutputStream out, @Nullable Object @Nullable ... args) {
        var engine = template.engine();
        return invokeWithOut(new OutputStreamOut(out, engine.charset(), engine.codecFactory()), args);
    }


    @Nullable
    public Object invokeWithOut(
            Charset charset, OutputStream out, @Nullable Object @Nullable ... args) {
        var engine = template.engine();
        return invokeWithOut(new OutputStreamOut(out, nvl(charset, engine.charset()), engine.codecFactory()), args);
    }
}
