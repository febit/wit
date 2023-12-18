// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit;

import jakarta.annotation.Nullable;
import org.febit.wit.core.Parser;
import org.febit.wit.exceptions.ParseException;
import org.febit.wit.exceptions.ScriptRuntimeException;
import org.febit.wit.exceptions.TemplateException;
import org.febit.wit.io.impl.DiscardOut;
import org.febit.wit.io.impl.OutputStreamOut;
import org.febit.wit.io.impl.WriterOut;
import org.febit.wit.lang.BreakpointListener;
import org.febit.wit.lang.InternedEncoding;
import org.febit.wit.lang.Out;
import org.febit.wit.lang.Resource;
import org.febit.wit.lang.ast.TemplateAST;

import java.io.OutputStream;
import java.io.Writer;
import java.util.Map;

/**
 * @author zqq90
 */
public class Template {

    private final Engine engine;
    private final String name;
    private final Resource resource;

    private volatile TemplateAST ast;

    Template(Engine engine, String name, Resource resource) {
        this.engine = engine;
        this.name = name;
        this.resource = resource;
    }

    /**
     * Reload this template.
     *
     * @throws ParseException when unable to parse
     * @since 1.4.0
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

    private synchronized TemplateAST prepareAst(boolean forceRebuild) {
        TemplateAST myAst = this.ast;
        if (forceRebuild || isAstExpired(myAst)) {
            myAst = Parser.parse(this);
            this.ast = myAst;
        }
        return myAst;
    }

    private boolean isAstExpired(TemplateAST myAst) {
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
     * @since 3.0.0
     */
    protected Context merge0(@Nullable Vars vars, @Nullable Out out, @Nullable BreakpointListener listener) {
        if (vars == null) {
            vars = Vars.EMPTY;
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
     * @param outputStream out
     * @return Context
     * @throws ScriptRuntimeException when script runtime exception
     * @throws ParseException         when unable to parse
     */
    public Context merge(final OutputStream outputStream) {
        return merge0(null, new OutputStreamOut(outputStream, engine), null);
    }

    /**
     * Merge this template.
     *
     * @param output out
     * @param encoding encoding
     * @return Context
     * @throws ScriptRuntimeException when script runtime exception
     * @throws ParseException         when unable to parse
     */
    public Context merge(final OutputStream output, final String encoding) {
        var out = new OutputStreamOut(output, InternedEncoding.intern(encoding), engine);
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
    public Context merge(final Writer writer) {
        return merge0(null, new WriterOut(writer, engine), null);
    }

    /**
     * Merge this template.
     *
     * @param vars vars
     * @param outputStream outputStream
     * @return Context
     * @throws ScriptRuntimeException when script runtime exception
     * @throws ParseException         when unable to parse
     */
    public Context merge(final Map<String, Object> vars, final OutputStream outputStream) {
        return merge0(Vars.of(vars), new OutputStreamOut(outputStream, engine), null);
    }

    /**
     * Merge this template.
     *
     * @param vars vars
     * @param out out
     * @param encoding encoding
     * @return Context
     * @throws ScriptRuntimeException when script runtime exception
     * @throws ParseException         when unable to parse
     */
    public Context merge(final Map<String, Object> vars, final OutputStream out, final String encoding) {
        return merge0(Vars.of(vars), new OutputStreamOut(out, InternedEncoding.intern(encoding), engine), null);
    }

    /**
     * Merge this template.
     *
     * @param vars vars
     * @param writer writer
     * @return Context
     * @throws ScriptRuntimeException when script runtime exception
     * @throws ParseException         when unable to parse
     */
    public Context merge(final Map<String, Object> vars, final Writer writer) {
        return merge0(Vars.of(vars), new WriterOut(writer, engine), null);
    }

    /**
     * Merge this template.
     *
     * @param vars vars
     * @param out out
     * @return Context
     * @throws ScriptRuntimeException when script runtime exception
     * @throws ParseException         when unable to parse
     */
    public Context merge(final Vars vars, final OutputStream out) {
        return merge0(vars, new OutputStreamOut(out, engine), null);
    }

    /**
     * Merge this template.
     *
     * @param vars vars
     * @param out out
     * @param encoding encoding
     * @return Context
     * @throws ScriptRuntimeException when script runtime exception
     * @throws ParseException         when unable to parse
     */
    public Context merge(final Vars vars, final OutputStream out, final String encoding) {
        return merge0(vars, new OutputStreamOut(out, InternedEncoding.intern(encoding), engine), null);
    }

    /**
     * Merge this template.
     *
     * @param vars vars
     * @param writer writer
     * @return Context
     * @throws ScriptRuntimeException when script runtime exception
     * @throws ParseException         when unable to parse
     */
    public Context merge(final Vars vars, final Writer writer) {
        return merge0(vars, new WriterOut(writer, engine), null);
    }

    /**
     * Merge this template.
     *
     * @param out out
     * @return Context
     * @throws ScriptRuntimeException when script runtime exception
     * @throws ParseException         when unable to parse
     * @since 1.4.0
     */
    public Context merge(final Out out) {
        return merge0(null, out, null);
    }

    /**
     * Merge this template, and discard outputs.
     *
     * @return Context
     * @throws ScriptRuntimeException when script runtime exception
     * @throws ParseException         when unable to parse
     * @since 2.4.0
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
     * @since 2.4.0
     */
    public Context merge(final Map<String, Object> vars) {
        return merge0(Vars.of(vars), null, null);
    }

    /**
     * Merge this template, and discard outputs.
     *
     * @param vars vars
     * @return Context
     * @throws ScriptRuntimeException when script runtime exception
     * @throws ParseException         when unable to parse
     * @since 2.4.0
     */
    public Context merge(final Vars vars) {
        return merge0(vars,  null, null);
    }

    /**
     * Merge this template.
     *
     * @param vars vars
     * @param out out
     * @return Context
     * @throws ScriptRuntimeException when script runtime exception
     * @throws ParseException         when unable to parse
     */
    public Context merge(final Vars vars, final Out out) {
       return merge0(vars, out, null);
    }

    public Context mergeToContext(final InternalContext context, final Vars vars) {
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
     * @param vars vars
     * @param  out out
     * @param listener listener
     * @return Context
     * @throws ScriptRuntimeException when script runtime exception
     * @throws ParseException         when unable to parse
     */
    public Context debug(final Vars vars, final Out out, final BreakpointListener listener) {
        return merge0(vars, out, listener);
    }

    /**
     * Debug this template, and discard outputs.
     *
     * @param vars vars
     * @param listener breakpoint listener
     * @return Context
     * @throws ScriptRuntimeException when script runtime exception
     * @throws ParseException         when unable to parse
     * @since 2.4.0
     */
    public Context debug(final Vars vars, final BreakpointListener listener) {
        return merge0(vars, null, listener);
    }

    /**
     * Debug this template, and discard outputs.
     *
     * @param out out
     * @param listener breakpoint listener
     * @return Context
     * @throws ScriptRuntimeException when script runtime exception
     * @throws ParseException         when unable to parse
     * @since 2.5.0
     */
    public Context debug(final Out out, final BreakpointListener listener) {
        return merge0(null, out, listener);
    }

    /**
     * Debug this template, and discard outputs.
     *
     * @param listener breakpoint listener
     * @return Context
     * @throws ScriptRuntimeException when script runtime exception
     * @throws ParseException         when unable to parse
     * @since 2.5.0
     */
    public Context debug(final BreakpointListener listener) {
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
    public long getLastModified() {
        var myAst = this.ast;
        return myAst != null ? myAst.getCreatedAt() : -1L;
    }

    /**
     * Get engine.
     *
     * @return template engine
     * @since 2.5.0
     */
    public Engine getEngine() {
        return engine;
    }

    /**
     * Get template name.
     *
     * @return template name
     * @since 2.5.0
     */
    public String getName() {
        return name;
    }

    /**
     * Get resource for this template.
     *
     * @return resource
     * @since 2.5.0
     */
    public Resource getResource() {
        return resource;
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Template)) {
            return false;
        }
        var other = (Template) obj;
        return this.engine == other.engine
                && this.name.equals(other.name);
    }

    private TemplateException completeException(final Exception exception) {
        return ((exception instanceof TemplateException)
                ? ((TemplateException) exception)
                : new ScriptRuntimeException(exception)).setTemplate(this);
    }
}
