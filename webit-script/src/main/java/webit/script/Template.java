// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script;

import java.io.OutputStream;
import java.io.Writer;
import java.util.Map;
import webit.script.core.Parser;
import webit.script.core.ast.TemplateAST;
import webit.script.exceptions.ParseException;
import webit.script.exceptions.ScriptRuntimeException;
import webit.script.exceptions.UncheckedException;
import webit.script.io.Out;
import webit.script.io.impl.OutputStreamOut;
import webit.script.io.impl.WriterOut;
import webit.script.loaders.Resource;
import webit.script.util.EncodingPool;
import webit.script.util.keyvalues.KeyValues;
import webit.script.util.keyvalues.KeyValuesUtil;

/**
 *
 * @author Zqq
 */
public final class Template {

    public final Engine engine;
    public final String name;
    public final Resource resource;

    private final Object reloadLock = new Object();

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
        TemplateAST ast = this.ast;
        synchronized (this.reloadLock) {
            if (force || ast == null || this.resource.isModified()) {
                ast = new Parser().parseTemplate(this);
                this.ast = ast;
                this.lastModified = System.currentTimeMillis();
            }
        }
        return ast;
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
     * @param outputStream
     * @param encoding
     * @return Context
     * @throws ScriptRuntimeException
     * @throws ParseException
     */
    public Context merge(final OutputStream outputStream, final String encoding) throws ScriptRuntimeException, ParseException {
        return merge(KeyValuesUtil.EMPTY_KEY_VALUES, new OutputStreamOut(outputStream, encoding != null ? EncodingPool.intern(encoding) : engine.getEncoding(), engine.getCoderFactory()));
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
     * @param root
     * @param outputStream
     * @return Context
     * @throws ScriptRuntimeException
     * @throws ParseException
     */
    public Context merge(final Map<String, Object> root, final OutputStream outputStream) throws ScriptRuntimeException, ParseException {
        return merge(KeyValuesUtil.wrap(root), new OutputStreamOut(outputStream, engine));
    }

    /**
     * Merge this template.
     *
     * @param root
     * @param outputStream
     * @param encoding
     * @return Context
     * @throws ScriptRuntimeException
     * @throws ParseException
     */
    public Context merge(final Map<String, Object> root, final OutputStream outputStream, final String encoding) throws ScriptRuntimeException, ParseException {
        return merge(KeyValuesUtil.wrap(root), new OutputStreamOut(outputStream, encoding != null ? EncodingPool.intern(encoding) : engine.getEncoding(), engine.getCoderFactory()));
    }

    /**
     * Merge this template.
     *
     * @param root
     * @param writer
     * @return Context
     * @throws ScriptRuntimeException
     * @throws ParseException
     */
    public Context merge(final Map<String, Object> root, final Writer writer) throws ScriptRuntimeException, ParseException {
        return merge(KeyValuesUtil.wrap(root), new WriterOut(writer, engine));
    }

    /**
     * Merge this template.
     *
     * @param root
     * @param outputStream
     * @return Context
     * @throws ScriptRuntimeException
     * @throws ParseException
     */
    public Context merge(final KeyValues root, final OutputStream outputStream) throws ScriptRuntimeException, ParseException {
        return merge(root, new OutputStreamOut(outputStream, engine));
    }

    /**
     * Merge this template.
     *
     * @param root
     * @param outputStream
     * @param encoding
     * @return Context
     * @throws ScriptRuntimeException
     * @throws ParseException
     */
    public Context merge(final KeyValues root, final OutputStream outputStream, final String encoding) throws ScriptRuntimeException, ParseException {
        return merge(root, new OutputStreamOut(outputStream, encoding != null ? EncodingPool.intern(encoding) : engine.getEncoding(), engine.getCoderFactory()));
    }

    /**
     * Merge this template.
     *
     * @param root
     * @param writer
     * @return Context
     * @throws ScriptRuntimeException
     * @throws ParseException
     */
    public Context merge(final KeyValues root, final Writer writer) throws ScriptRuntimeException, ParseException {
        return merge(root, new WriterOut(writer, engine));
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
        return this.merge(KeyValuesUtil.EMPTY_KEY_VALUES, out);
    }

    /**
     * Merge this template.
     *
     * @param root
     * @param out
     * @return Context
     * @throws ScriptRuntimeException
     * @throws ParseException
     */
    public Context merge(final KeyValues root, final Out out) throws ScriptRuntimeException, ParseException {
        try {
            TemplateAST ast = this.ast;
            if (ast == null || this.resource.isModified()) {
                ast = parse(false);
            }
            return ast.execute(new Context(this, out, root));
        } catch (Exception e) {
            throw completeException(e);
        }
    }

    /**
     * Merge this template as a child Template, used by include/import.
     *
     * @since 1.4.0
     * @param parent
     * @param params
     * @return Context
     * @throws ScriptRuntimeException
     * @throws ParseException
     */
    public Context mergeForInlude(final Context parent, KeyValues params) throws ScriptRuntimeException, ParseException {
        try {
            TemplateAST ast = this.ast;
            if (ast == null || this.resource.isModified()) {
                ast = parse(false);
            }
            return ast.execute(new Context(parent, this, params));
        } catch (Exception e) {
            throw completeException(e);
        }
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

    private UncheckedException completeException(final Exception exception) {
        return (exception instanceof UncheckedException)
                ? ((UncheckedException) exception).setTemplate(this)
                : new ScriptRuntimeException(exception).setTemplate(this);
    }
}
