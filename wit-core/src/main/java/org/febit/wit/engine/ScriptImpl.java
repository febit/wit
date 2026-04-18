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
package org.febit.wit.engine;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.febit.wit.Context;
import org.febit.wit.Script;
import org.febit.wit.Vars;
import org.febit.wit.Wit;
import org.febit.wit.exception.ScriptEvaluateException;
import org.febit.wit.exception.ScriptException;
import org.febit.wit.exception.ScriptParseException;
import org.febit.wit.io.Out;
import org.febit.wit.io.Source;
import org.febit.wit.io.out.DiscardOut;
import org.febit.wit.ir.ScriptIR;
import org.febit.wit.runtime.RuntimeContext;
import org.febit.wit.runtime.heap.GenericHeap;
import org.jspecify.annotations.Nullable;

import static org.febit.wit.util.Defaults.nvl;

@SuppressWarnings({
        "UnusedReturnValue"
})
@Accessors(fluent = true)
public class ScriptImpl implements Script {

    @Getter
    private final Wit engine;
    @Getter
    private final String path;
    @Getter
    private final Source source;

    @Nullable
    private volatile ScriptIR compiledIR;

    public ScriptImpl(Wit engine, String path, Source source) {
        this.engine = engine;
        this.path = path;
        this.source = source;
    }

    /**
     * Reload this script.
     *
     * @throws ScriptParseException when unable to parse
     */
    @Override
    public void reload() {
        ir(true);
    }

    private ScriptIR ir() {
        var ir = this.compiledIR;
        if (!isIrExpired(ir)) {
            return ir;
        }
        return ir(false);
    }

    private synchronized ScriptIR ir(boolean renew) {
        var ir = this.compiledIR;
        if (renew || isIrExpired(ir)) {
            ir = engine.parserFactory()
                    .get(new ParseContext(engine, path, source))
                    .parse();
            this.compiledIR = ir;
        }
        return ir;
    }

    private boolean isIrExpired(@Nullable ScriptIR ir) {
        if (ir == null) {
            return true;
        }
        return ir.sourceVersion() != this.source.version();
    }

    @Override
    public Context eval(
            @Nullable Vars inputs,
            @Nullable Out out,
            @Nullable Heap local,
            @Nullable BreakpointHandler breakpointHandler
    ) {
        try {
            var ir = ir();
            var context = new RuntimeContext(
                    this,
                    ir.createVariableHeap(),
                    nvl(inputs, Vars::empty),
                    nvl(out, DiscardOut::get),
                    nvl(local, GenericHeap::local),
                    breakpointHandler
            );
            ir.execute(context);
            return context;
        } catch (Exception e) {
            var se = (e instanceof ScriptException ex) ? ex
                    : new ScriptEvaluateException(e);
            se.script(this);
            throw se;
        }
    }

    @Override
    public void reset() {
        this.compiledIR = null;
    }

    @Override
    public int hashCode() {
        return this.path.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ScriptImpl other)) {
            return false;
        }
        return this.engine == other.engine
                && this.path.equals(other.path);
    }

}
