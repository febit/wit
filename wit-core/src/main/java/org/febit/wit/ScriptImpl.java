// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.febit.wit.exception.ParseException;
import org.febit.wit.exception.ScriptEvaluateException;
import org.febit.wit.exception.ScriptException;
import org.febit.wit.io.Out;
import org.febit.wit.io.Source;
import org.febit.wit.parser.Parser;
import org.febit.wit.runtime.BreakpointHandler;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.ast.ScriptAST;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

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
    private volatile ScriptAST ast;

    public ScriptImpl(Wit engine, String path, Source source) {
        this.engine = engine;
        this.path = path;
        this.source = source;
    }

    /**
     * Reload this script.
     *
     * @throws ParseException when unable to parse
     */
    @Override
    public void reload() {
        prepareAst(true);
    }

    private ScriptAST prepareAst() {
        var myAst = this.ast;
        if (!isAstExpired(myAst)) {
            return myAst;
        }
        return prepareAst(false);
    }

    private synchronized ScriptAST prepareAst(boolean renew) {
        var myAst = this.ast;
        if (renew || isAstExpired(myAst)) {
            myAst = Parser.parse(this);
            this.ast = myAst;
        }
        return myAst;
    }

    private boolean isAstExpired(@Nullable ScriptAST myAst) {
        if (myAst == null) {
            return true;
        }
        return myAst.sourceVersion() != this.source.version();
    }

    /**
     * Evaluate this script.
     *
     * @param inputs            vars
     * @param out               out
     * @param breakpointHandler listener
     * @return Context
     * @throws ScriptEvaluateException when script runtime exception
     * @throws ParseException          when unable to parse
     */
    public Context eval(Vars inputs, Out out, @Nullable BreakpointHandler breakpointHandler) {
        Objects.requireNonNull(inputs, "inputs is required");
        Objects.requireNonNull(out, "out is required");

        try {
            return Parser.parse(this)
                    .execute(this, out, inputs, breakpointHandler);
        } catch (Exception e) {
            throw completeException(e);
        }
    }

    @Override
    public Context merge(InternalContext target, Vars inputs) {
        try {
            return prepareAst()
                    .execute(this, target, inputs);
        } catch (Exception e) {
            throw completeException(e);
        }
    }

    @Override
    public void reset() {
        this.ast = null;
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

    private ScriptException completeException(Exception exception) {
        return ((exception instanceof ScriptException ex) ? ex
                : new ScriptEvaluateException(exception)).setScript(this);
    }
}
