// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit;

import lombok.RequiredArgsConstructor;
import org.febit.wit.io.Out;
import org.febit.wit.io.out.DiscardOut;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.WitFunction;
import org.febit.wit.runtime.heap.GenericHeap;
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
public final class ExportedFunction {

    private final Script script;
    private final WitFunction function;
    private final Out defaultOut;

    public ExportedFunction(Script container, WitFunction function) {
        this(container, function, DiscardOut.get());
    }

    public ExportedFunction(
            Script container,
            WitFunction function,
            Charset charset,
            boolean isByteStream
    ) {
        this(container, function, new DiscardOut(charset, isByteStream));
    }

    private InternalContext createContext(Out out) {
        var variables = VariableHeap.empty();
        var local = GenericHeap.local();
        return new InternalContext(script, variables, Vars.empty(), out, local, null);
    }

    @Nullable
    private Object doApply(InternalContext context, @Nullable Object @Nullable ... args) {
        return this.function.apply(context, args);
    }

    @Nullable
    public Object apply(@Nullable Object @Nullable ... args) {
        return doApply(createContext(defaultOut), args);
    }

    @Nullable
    public Object applyWithOut(Out out, @Nullable Object @Nullable ... args) {
        return doApply(createContext(out), args);
    }

    @Nullable
    public Object applyWithOut(Writer writer, @Nullable Object @Nullable ... args) {
        return applyWithOut(script.engine().asOut(writer), args);
    }

    @Nullable
    public Object applyWithOut(OutputStream out, @Nullable Object @Nullable ... args) {
        return applyWithOut(script.engine().asOut(out), args);
    }
}
