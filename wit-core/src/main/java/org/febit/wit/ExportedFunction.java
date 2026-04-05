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

import org.febit.wit.io.Out;
import org.febit.wit.runtime.RuntimeContext;
import org.febit.wit.runtime.WitFunction;
import org.febit.wit.runtime.heap.GenericHeap;
import org.febit.wit.runtime.heap.VariableHeap;
import org.jspecify.annotations.Nullable;

import java.io.OutputStream;
import java.io.Writer;

/**
 * Exported function.
 */
public record ExportedFunction(
        WitFunction function,
        Script script,
        Out out
) {

    public ExportedFunction withOut(Out out) {
        return new ExportedFunction(function, script, out);
    }

    public ExportedFunction withOut(Writer writer) {
        return withOut(script.engine().asOut(writer));
    }

    public ExportedFunction withOut(OutputStream out) {
        return withOut(script.engine().asOut(out));
    }

    @Nullable
    public Object apply(@Nullable Object @Nullable ... args) {
        var context = new RuntimeContext(
                script,
                VariableHeap.empty(),
                Vars.empty(),
                out,
                GenericHeap.local(),
                null
        );
        return this.function.apply(context, args);
    }
}
