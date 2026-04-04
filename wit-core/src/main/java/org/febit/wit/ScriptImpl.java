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

import lombok.Getter;
import lombok.experimental.Accessors;
import org.febit.wit.exception.ScriptEvaluateException;
import org.febit.wit.exception.ScriptException;
import org.febit.wit.exception.ScriptParseException;
import org.febit.wit.io.Out;
import org.febit.wit.io.Source;
import org.febit.wit.parser.Parser;
import org.febit.wit.runtime.BreakpointHandler;
import org.febit.wit.runtime.RuntimeContext;
import org.febit.wit.runtime.ast.ScriptAST;
import org.febit.wit.runtime.heap.GenericHeap;
import org.febit.wit.runtime.heap.Heap;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

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
    private volatile ScriptAST parsedAst;

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
        ast(true);
    }

    private ScriptAST ast() {
        var ast = this.parsedAst;
        if (!isAstExpired(ast)) {
            return ast;
        }
        return ast(false);
    }

    private synchronized ScriptAST ast(boolean renew) {
        var ast = this.parsedAst;
        if (renew || isAstExpired(ast)) {
            ast = Parser.parse(this);
            this.parsedAst = ast;
        }
        return ast;
    }

    private boolean isAstExpired(@Nullable ScriptAST ast) {
        if (ast == null) {
            return true;
        }
        return ast.sourceVersion() != this.source.version();
    }

    @Override
    public Context eval(
            Vars inputs,
            Out out,
            @Nullable Heap local,
            @Nullable BreakpointHandler breakpointHandler
    ) {
        Objects.requireNonNull(inputs, "inputs is required");
        Objects.requireNonNull(out, "out is required");
        try {
            var ast = ast();
            var context = new RuntimeContext(
                    this,
                    ast.createVariableHeap(),
                    inputs,
                    out,
                    nvl(local, GenericHeap::local),
                    breakpointHandler
            );
            ast.execute(context);
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
        this.parsedAst = null;
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
