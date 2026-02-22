// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit;

import lombok.RequiredArgsConstructor;
import org.febit.wit.io.DiscardOut;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.function.FunctionDeclare;
import org.febit.wit.runtime.heap.GenricHeap;
import org.febit.wit.runtime.heap.VariableHeap;
import org.jspecify.annotations.Nullable;

import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.Charset;

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
        this(container, functionDeclare, DiscardOut.get());
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
        var variables = VariableHeap.empty();
        var local = GenricHeap.local();
        return new InternalContext(script, variables, Vars.empty(), out, local, null);
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
        return applyWithOut(script.wit().asOut(writer), args);
    }

    @Nullable
    public Object applyWithOut(
            OutputStream out, @Nullable Object @Nullable ... args) {
        return applyWithOut(script.wit().asOut(out), args);
    }
}
