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
import org.febit.wit.engine.BreakpointHandler;
import org.febit.wit.engine.Heap;
import org.febit.wit.exception.ScriptEvaluateException;
import org.febit.wit.exception.ScriptParseException;
import org.febit.wit.io.Out;
import org.febit.wit.io.Source;
import org.jspecify.annotations.Nullable;

import java.io.OutputStream;
import java.io.Writer;

public interface Script {

    Wit engine();

    String path();

    Source source();

    void reset();

    void reload();

    /**
     * Eval script.
     *
     * @param inputs            input vars
     * @param out               out
     * @param local             Heap to store local variables, may be null
     * @param breakpointHandler breakpoint handler, may be null
     * @return Context
     * @throws ScriptEvaluateException when script runtime exception
     */
    Context eval(
            @Nullable Vars inputs,
            @Nullable Out out,
            @Nullable Heap local,
            @Nullable BreakpointHandler breakpointHandler
    );

    @CheckReturnValue
    default Evaluator evaluator() {
        return Evaluator.of(this);
    }

    /**
     * Eval script.
     *
     * @return Context
     * @throws ScriptEvaluateException when script runtime exception
     * @throws ScriptParseException    when unable to parse
     */
    default Context eval() {
        return eval(null, null, null, null);
    }

    /**
     * Eval script.
     *
     * @param inputs input vars
     * @param output out
     * @return Context
     * @throws ScriptEvaluateException when script runtime exception
     * @throws ScriptParseException    when unable to parse
     */
    default Context eval(Vars inputs, OutputStream output) {
        var out = engine().asOut(output);
        return eval(inputs, out, null, null);
    }

    /**
     * Eval script.
     *
     * @param inputs input vars
     * @param writer writer
     * @return Context
     * @throws ScriptEvaluateException when script runtime exception
     * @throws ScriptParseException    when unable to parse
     */
    default Context eval(Vars inputs, Writer writer) {
        var out = engine().asOut(writer);
        return eval(inputs, out, null, null);
    }

    /**
     * Eval script.
     *
     * @param inputs input vars
     * @param out    out
     * @return Context
     * @throws ScriptEvaluateException when script runtime exception
     * @throws ScriptParseException    when unable to parse
     */
    default Context eval(Vars inputs, Out out) {
        return eval(inputs, out, null, null);
    }
}
