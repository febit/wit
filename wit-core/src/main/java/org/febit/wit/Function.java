// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit;

import lombok.RequiredArgsConstructor;
import org.febit.wit.io.DiscardOut;
import org.febit.wit.io.OutputStreamOut;
import org.febit.wit.io.WriterOut;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.function.FunctionDeclare;
import org.febit.wit.runtime.heap.LocalHeap;
import org.febit.wit.runtime.heap.VariantHeap;
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

    private final Script script;
    private final FunctionDeclare functionDeclare;
    private final Out defaultOut;

    public Function(Script container, FunctionDeclare functionDeclare) {
        this(container, functionDeclare, DiscardOut.INSTANCE);
    }

    public Function(
            Script container,
            FunctionDeclare functionDeclare,
            Charset charset,
            boolean isByteStream
    ) {
        this(container, functionDeclare, new DiscardOut(charset, isByteStream));
    }

    private InternalContext createContext(Out out) {
        var heap = VariantHeap.empty();
        var local = LocalHeap.create();
        return new InternalContext(script, out, Vars.empty(), heap, local, null);
    }

    private InternalContext createContext() {
        return createContext(defaultOut);
    }

    @Nullable
    private Object doApply(
            InternalContext context,
            @Nullable Object @Nullable ... args
    ) {
        return this.functionDeclare.apply(context, args);
    }

    @Nullable
    public Object apply(@Nullable Object @Nullable ... args) {
        return doApply(createContext(), args);
    }

    @Nullable
    public Object applyWithOut(Out out, @Nullable Object @Nullable ... args) {
        return doApply(createContext(out), args);
    }

    @Nullable
    public Object applyWithOut(Writer writer, @Nullable Object @Nullable ... args) {
        var engine = script.engine();
        return applyWithOut(new WriterOut(writer, engine.charset(), engine.codecFactory()), args);
    }

    @Nullable
    public Object applyWithOut(
            OutputStream out, @Nullable Object @Nullable ... args) {
        var engine = script.engine();
        return applyWithOut(new OutputStreamOut(out, engine.charset(), engine.codecFactory()), args);
    }

    @Nullable
    public Object applyWithOut(
            Charset charset, OutputStream out, @Nullable Object @Nullable ... args) {
        var engine = script.engine();
        return applyWithOut(new OutputStreamOut(out, nvl(charset, engine.charset()), engine.codecFactory()), args);
    }
}
