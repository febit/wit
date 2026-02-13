// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.febit.wit.core.Parser;
import org.febit.wit.exceptions.ParseException;
import org.febit.wit.exceptions.ScriptRuntimeException;
import org.febit.wit.exceptions.TemplateException;
import org.febit.wit.io.DiscardOut;
import org.febit.wit.io.OutputStreamOut;
import org.febit.wit.io.WriterOut;
import org.febit.wit.runtime.BreakpointListener;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.Resource;
import org.febit.wit.runtime.ast.TemplateAST;
import org.jspecify.annotations.Nullable;

import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Map;

import static org.febit.wit.util.Defaults.nvl;

@SuppressWarnings({
        "UnusedReturnValue"
})
@Accessors(fluent = true)
public class Template {

    @Getter
    private final Engine engine;
    @Getter
    private final String path;
    @Getter
    private final Resource resource;

    @Nullable
    private volatile TemplateAST ast;

    Template(Engine engine, String path, Resource resource) {
        this.engine = engine;
        this.path = path;
        this.resource = resource;
    }

    /**
     * Reload this template.
     *
     * @throws ParseException when unable to parse
     */
    public void reload() {
        prepareAst(true);
    }

    private TemplateAST prepareAst() {
        var myAst = this.ast;
        if (!isAstExpired(myAst)) {
            return myAst;
        }
        return prepareAst(false);
    }

    private synchronized TemplateAST prepareAst(boolean renew) {
        var myAst = this.ast;
        if (renew || isAstExpired(myAst)) {
            myAst = Parser.parse(this);
            this.ast = myAst;
        }
        return myAst;
    }

    private boolean isAstExpired(@Nullable TemplateAST myAst) {
        if (myAst == null) {
            return true;
        }
        return myAst.getResourceVersion() != this.resource.version();
    }

    /**
     * Debug this template.
     *
     * @param vars     vars
     * @param out      out
     * @param listener listener
     * @return Context
     * @throws ScriptRuntimeException when script runtime exception
     * @throws ParseException         when unable to parse
     */
    protected Context merge0(@Nullable Vars vars, @Nullable Out out, @Nullable BreakpointListener listener) {
        if (vars == null) {
            vars = Vars.empty();
        }
        if (out == null) {
            out = DiscardOut.INSTANCE;
        }
        try {
            return Parser.parse(this)
                    .execute(this, out, vars, listener);
        } catch (Exception e) {
            throw completeException(e);
        }
    }

    /**
     * Merge this template.
     *
     * @param output out
     * @return Context
     * @throws ScriptRuntimeException when script runtime exception
     * @throws ParseException         when unable to parse
     */
    public Context merge(OutputStream output) {
        return merge0(null, new OutputStreamOut(output, engine.charset(), engine.codecFactory()), null);
    }

    /**
     * Merge this template.
     *
     * @param output  out
     * @param charset charset
     * @return Context
     * @throws ScriptRuntimeException when script runtime exception
     * @throws ParseException         when unable to parse
     */
    public Context merge(OutputStream output, @Nullable Charset charset) {
        var out = new OutputStreamOut(output, nvl(charset, engine.charset()), engine.codecFactory());
        return merge0(null, out, null);
    }

    /**
     * Merge this template.
     *
     * @param writer writer
     * @return Context
     * @throws ScriptRuntimeException when script runtime exception
     * @throws ParseException         when unable to parse
     */
    public Context merge(Writer writer) {
        return merge0(null, new WriterOut(writer, engine.charset(), engine.codecFactory()), null);
    }

    /**
     * Merge this template.
     *
     * @param vars   vars
     * @param output output
     * @return Context
     * @throws ScriptRuntimeException when script runtime exception
     * @throws ParseException         when unable to parse
     */
    public Context merge(Map<String, Object> vars, OutputStream output) {
        return merge0(Vars.of(vars), new OutputStreamOut(output, engine.charset(), engine.codecFactory()), null);
    }

    /**
     * Merge this template.
     *
     * @param vars    vars
     * @param out     out
     * @param charset charset
     * @return Context
     * @throws ScriptRuntimeException when script runtime exception
     * @throws ParseException         when unable to parse
     */
    public Context merge(Map<String, Object> vars, OutputStream out, @Nullable Charset charset) {
        return merge0(
                Vars.of(vars),
                new OutputStreamOut(out, nvl(charset, engine.charset()), engine.codecFactory()),
                null
        );
    }

    /**
     * Merge this template.
     *
     * @param vars   vars
     * @param writer writer
     * @return Context
     * @throws ScriptRuntimeException when script runtime exception
     * @throws ParseException         when unable to parse
     */
    public Context merge(Map<String, Object> vars, Writer writer) {
        return merge0(
                Vars.of(vars),
                new WriterOut(writer, engine.charset(), engine.codecFactory()),
                null
        );
    }

    /**
     * Merge this template.
     *
     * @param vars vars
     * @param out  out
     * @return Context
     * @throws ScriptRuntimeException when script runtime exception
     * @throws ParseException         when unable to parse
     */
    public Context merge(Vars vars, OutputStream out) {
        return merge0(vars, new OutputStreamOut(out, engine.charset(), engine.codecFactory()), null);
    }

    /**
     * Merge this template.
     *
     * @param vars    vars
     * @param out     out
     * @param charset charset
     * @return Context
     * @throws ScriptRuntimeException when script runtime exception
     * @throws ParseException         when unable to parse
     */
    public Context merge(Vars vars, OutputStream out, @Nullable Charset charset) {
        return merge0(vars, new OutputStreamOut(out, nvl(charset, engine.charset()), engine.codecFactory()), null);
    }

    /**
     * Merge this template.
     *
     * @param vars   vars
     * @param writer writer
     * @return Context
     * @throws ScriptRuntimeException when script runtime exception
     * @throws ParseException         when unable to parse
     */
    public Context merge(Vars vars, Writer writer) {
        return merge0(vars, new WriterOut(writer, engine.charset(), engine.codecFactory()), null);
    }

    /**
     * Merge this template.
     *
     * @param out out
     * @return Context
     * @throws ScriptRuntimeException when script runtime exception
     * @throws ParseException         when unable to parse
     */
    public Context merge(Out out) {
        return merge0(null, out, null);
    }

    /**
     * Merge this template, and discard outputs.
     *
     * @return Context
     * @throws ScriptRuntimeException when script runtime exception
     * @throws ParseException         when unable to parse
     */
    public Context merge() {
        return merge0(null, null, null);
    }

    /**
     * Merge this template, and discard outputs.
     *
     * @param vars vars
     * @return Context
     * @throws ScriptRuntimeException when script runtime exception
     * @throws ParseException         when unable to parse
     */
    public Context merge(Map<?, ?> vars) {
        return merge0(Vars.of(vars), null, null);
    }

    /**
     * Merge this template, and discard outputs.
     *
     * @param vars vars
     * @return Context
     * @throws ScriptRuntimeException when script runtime exception
     * @throws ParseException         when unable to parse
     */
    public Context merge(Vars vars) {
        return merge0(vars, null, null);
    }

    /**
     * Merge this template.
     *
     * @param vars vars
     * @param out  out
     * @return Context
     * @throws ScriptRuntimeException when script runtime exception
     * @throws ParseException         when unable to parse
     */
    public Context merge(Vars vars, Out out) {
        return merge0(vars, out, null);
    }

    public Context mergeToContext(InternalContext context, Vars vars) {
        try {
            return prepareAst()
                    .execute(this, context, vars);
        } catch (Exception e) {
            throw completeException(e);
        }
    }

    /**
     * Debug this template.
     *
     * @param vars     vars
     * @param out      out
     * @param listener listener
     * @return Context
     * @throws ScriptRuntimeException when script runtime exception
     * @throws ParseException         when unable to parse
     */
    public Context debug(Vars vars, Out out, BreakpointListener listener) {
        return merge0(vars, out, listener);
    }

    /**
     * Debug this template, and discard outputs.
     *
     * @param vars     vars
     * @param listener breakpoint listener
     * @return Context
     * @throws ScriptRuntimeException when script runtime exception
     * @throws ParseException         when unable to parse
     */
    public Context debug(Vars vars, BreakpointListener listener) {
        return merge0(vars, null, listener);
    }

    /**
     * Debug this template, and discard outputs.
     *
     * @param out      out
     * @param listener breakpoint listener
     * @return Context
     * @throws ScriptRuntimeException when script runtime exception
     * @throws ParseException         when unable to parse
     */
    public Context debug(Out out, BreakpointListener listener) {
        return merge0(null, out, listener);
    }

    /**
     * Debug this template, and discard outputs.
     *
     * @param listener breakpoint listener
     * @return Context
     * @throws ScriptRuntimeException when script runtime exception
     * @throws ParseException         when unable to parse
     */
    public Context debug(BreakpointListener listener) {
        return merge0(null, null, listener);
    }

    public void reset() {
        this.ast = null;
    }

    /**
     * Get the time that the template AST was last modified.
     *
     * @return the last modified time, measured in milliseconds
     */
    public long lastModified() {
        var myAst = this.ast;
        return myAst != null ? myAst.getCreatedAt() : -1L;
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
        if (!(obj instanceof Template other)) {
            return false;
        }
        return this.engine == other.engine
                && this.path.equals(other.path);
    }

    private TemplateException completeException(Exception exception) {
        return ((exception instanceof TemplateException ex) ? ex
                : new ScriptRuntimeException(exception)).setTemplate(this);
    }
}
