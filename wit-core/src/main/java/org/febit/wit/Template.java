// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit;

import java.io.OutputStream;
import java.io.Writer;
import java.util.Map;
import org.febit.wit.core.Parser;
import org.febit.wit.core.ast.TemplateAST;
import org.febit.wit.debug.BreakPointListener;
import org.febit.wit.exceptions.ParseException;
import org.febit.wit.exceptions.ScriptRuntimeException;
import org.febit.wit.exceptions.TemplateException;
import org.febit.wit.io.Out;
import org.febit.wit.io.impl.DiscardOut;
import org.febit.wit.io.impl.OutputStreamOut;
import org.febit.wit.io.impl.WriterOut;
import org.febit.wit.loaders.Resource;
import org.febit.wit.util.InternedEncoding;
import org.febit.wit.util.KeyValuesUtil;

/**
 *
 * @author zqq90
 */
public final class Template {

    public final Engine engine;
    public final String name;
    public final Resource resource;

    private TemplateAST ast;
    private long lastModified;

    Template(Engine engine, String name, Resource resource) {
        this.engine = engine;
        this.name = name;
        this.resource = resource;
    }

    /**
     * Reload this template.
     *
     * @since 1.4.0
     * @throws ParseException
     */
    public void reload() throws ParseException {
        parse(true);
    }

    private TemplateAST parse(boolean force) throws ParseException {
        TemplateAST myAst = this.ast;
        synchronized (this) {
            if (force || myAst == null || this.resource.isModified()) {
                myAst = Parser.parse(this);
                this.ast = myAst;
                this.lastModified = System.currentTimeMillis();
            }
        }
        return myAst;
    }

    /**
     * Merge this template.
     *
     * @param outputStream
     * @return Context
     * @throws ScriptRuntimeException
     * @throws ParseException
     */
    public Context merge(final OutputStream outputStream) throws ScriptRuntimeException, ParseException {
        return merge(KeyValuesUtil.EMPTY_KEY_VALUES, new OutputStreamOut(outputStream, engine));
    }

    /**
     * Merge this template.
     *
     * @param out
     * @param encoding
     * @return Context
     * @throws ScriptRuntimeException
     * @throws ParseException
     */
    public Context merge(final OutputStream out, final String encoding) throws ScriptRuntimeException, ParseException {
        return merge(KeyValuesUtil.EMPTY_KEY_VALUES, new OutputStreamOut(out, InternedEncoding.intern(encoding), engine));
    }

    /**
     * Merge this template.
     *
     * @param writer
     * @return Context
     * @throws ScriptRuntimeException
     * @throws ParseException
     */
    public Context merge(final Writer writer) throws ScriptRuntimeException, ParseException {
        return merge(KeyValuesUtil.EMPTY_KEY_VALUES, new WriterOut(writer, engine));
    }

    /**
     * Merge this template.
     *
     * @param vars
     * @param outputStream
     * @return Context
     * @throws ScriptRuntimeException
     * @throws ParseException
     */
    public Context merge(final Map<String, Object> vars, final OutputStream outputStream) throws ScriptRuntimeException, ParseException {
        return merge(KeyValuesUtil.wrap(vars), new OutputStreamOut(outputStream, engine));
    }

    /**
     * Merge this template.
     *
     * @param vars
     * @param out
     * @param encoding
     * @return Context
     * @throws ScriptRuntimeException
     * @throws ParseException
     */
    public Context merge(final Map<String, Object> vars, final OutputStream out, final String encoding) throws ScriptRuntimeException, ParseException {
        return merge(KeyValuesUtil.wrap(vars), new OutputStreamOut(out, InternedEncoding.intern(encoding), engine));
    }

    /**
     * Merge this template.
     *
     * @param vars
     * @param writer
     * @return Context
     * @throws ScriptRuntimeException
     * @throws ParseException
     */
    public Context merge(final Map<String, Object> vars, final Writer writer) throws ScriptRuntimeException, ParseException {
        return merge(KeyValuesUtil.wrap(vars), new WriterOut(writer, engine));
    }

    /**
     * Merge this template.
     *
     * @param vars
     * @param out
     * @return Context
     * @throws ScriptRuntimeException
     * @throws ParseException
     */
    public Context merge(final Vars vars, final OutputStream out) throws ScriptRuntimeException, ParseException {
        return merge(vars, new OutputStreamOut(out, engine));
    }

    /**
     * Merge this template.
     *
     * @param vars
     * @param out
     * @param encoding
     * @return Context
     * @throws ScriptRuntimeException
     * @throws ParseException
     */
    public Context merge(final Vars vars, final OutputStream out, final String encoding) throws ScriptRuntimeException, ParseException {
        return merge(vars, new OutputStreamOut(out, InternedEncoding.intern(encoding), engine));
    }

    /**
     * Merge this template.
     *
     * @param vars
     * @param writer
     * @return Context
     * @throws ScriptRuntimeException
     * @throws ParseException
     */
    public Context merge(final Vars vars, final Writer writer) throws ScriptRuntimeException, ParseException {
        return merge(vars, new WriterOut(writer, engine));
    }

    /**
     * Merge this template.
     *
     * @since 1.4.0
     * @param out
     * @return Context
     * @throws ScriptRuntimeException
     * @throws ParseException
     */
    public Context merge(final Out out) throws ScriptRuntimeException, ParseException {
        return merge(KeyValuesUtil.EMPTY_KEY_VALUES, out);
    }

    /**
     * Merge this template, and discard outputs.
     *
     * @since 2.4.0
     * @return Context
     * @throws ScriptRuntimeException
     * @throws ParseException
     */
    public Context merge() throws ScriptRuntimeException, ParseException {
        return merge(KeyValuesUtil.EMPTY_KEY_VALUES);
    }

    /**
     * Merge this template, and discard outputs.
     *
     * @since 2.4.0
     * @param vars
     * @return Context
     * @throws ScriptRuntimeException
     * @throws ParseException
     */
    public Context merge(final Map<String, Object> vars) throws ScriptRuntimeException, ParseException {
        return merge(KeyValuesUtil.wrap(vars));
    }

    /**
     * Merge this template, and discard outputs.
     *
     * @since 2.4.0
     * @param vars
     * @return Context
     * @throws ScriptRuntimeException
     * @throws ParseException
     */
    public Context merge(final Vars vars) throws ScriptRuntimeException, ParseException {
        return merge(KeyValuesUtil.EMPTY_KEY_VALUES, DiscardOut.INSTANCE);
    }

    /**
     * Merge this template.
     *
     * @param vars
     * @param out
     * @return Context
     * @throws ScriptRuntimeException
     * @throws ParseException
     */
    public Context merge(final Vars vars, final Out out) throws ScriptRuntimeException, ParseException {
        try {
            final TemplateAST myAst;
            return (((myAst = this.ast) == null || this.resource.isModified())
                    ? parse(false)
                    : myAst)
                    .execute(this, out, vars);
        } catch (Exception e) {
            throw completeException(e);
        }
    }

    public Context mergeToContext(final InternalContext context, final Vars vars) throws ScriptRuntimeException, ParseException {
        try {
            final TemplateAST myAst;
            return (((myAst = this.ast) == null || this.resource.isModified())
                    ? parse(false)
                    : myAst)
                    .execute(this, context, vars);
        } catch (Exception e) {
            throw completeException(e);
        }
    }

    public Context debug(final Vars vars, final Out out, final BreakPointListener listener) throws ScriptRuntimeException, ParseException {
        try {
            return Parser.parse(this, listener)
                    .execute(this, out, vars);
        } catch (Exception e) {
            throw completeException(e);
        }
    }

    /**
     * Debug this template, and discard outputs.
     *
     * @since 2.4.0
     * @param vars
     * @param listener
     * @return Context
     * @throws ScriptRuntimeException
     * @throws ParseException
     */
    public Context debug(final Vars vars, final BreakPointListener listener) throws ScriptRuntimeException, ParseException {
        return debug(vars, DiscardOut.INSTANCE, listener);
    }

    public void reset() {
        this.ast = null;
        this.lastModified = 0;
    }

    public long getLastModified() {
        return this.lastModified;
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
        if (obj == null || !(obj instanceof Template)) {
            return false;
        }
        Template other = (Template) obj;
        return this.engine == other.engine
                && this.name.equals(other.name);
    }

    private TemplateException completeException(final Exception exception) {
        return ((exception instanceof TemplateException)
                ? ((TemplateException) exception)
                : new ScriptRuntimeException(exception)).setTemplate(this);
    }
}
