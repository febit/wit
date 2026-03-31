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

import edu.umd.cs.findbugs.annotations.CheckReturnValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.Tolerate;
import org.febit.wit.io.Out;
import org.febit.wit.io.out.DiscardOut;
import org.febit.wit.runtime.BreakpointHandler;
import org.jspecify.annotations.Nullable;

import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.function.Supplier;

import static org.febit.wit.util.Defaults.nvl;

@Getter
@Setter(onMethod_ = {@CheckReturnValue})
@Accessors(fluent = true, chain = true)
@RequiredArgsConstructor(
        staticName = "of",
        onConstructor_ = {@CheckReturnValue}
)
public class Evaluator {

    @lombok.NonNull
    private final Script script;

    @Nullable
    private Vars inputs;

    @Nullable
    private Charset charset;

    @Nullable
    private Supplier<Out> out;

    @Nullable
    private BreakpointHandler breakpointHandler;

    @Tolerate
    @CheckReturnValue
    public Evaluator out(Writer writer) {
        return this.out(() -> script.engine().asOut(writer, charset));
    }

    @Tolerate
    @CheckReturnValue
    public Evaluator out(OutputStream output) {
        return this.out(() -> script.engine().asOut(output, charset));
    }

    public Context eval() {
        return script().eval(
                nvl(inputs, Vars::empty),
                nvl(out, (Supplier<Out>) DiscardOut::get).get(),
                breakpointHandler
        );
    }

}
