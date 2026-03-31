/*
 * Copyright 2013-present febit.org (support@febit.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

    private InternalContext contextForCaller(Out out) {
        return new InternalContext(
                script,
                VariableHeap.empty(),
                Vars.empty(),
                out,
                GenericHeap.local(),
                null
        );
    }

    @Nullable
    private Object doApply(InternalContext context, @Nullable Object @Nullable ... args) {
        return this.function.apply(context, args);
    }

    @Nullable
    public Object apply(@Nullable Object @Nullable ... args) {
        return doApply(contextForCaller(defaultOut), args);
    }

    @Nullable
    public Object applyWithOut(Out out, @Nullable Object @Nullable ... args) {
        return doApply(contextForCaller(out), args);
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
