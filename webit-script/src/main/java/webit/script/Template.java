// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Map;
import webit.script.core.Parser;
import webit.script.core.ast.TemplateAST;
import webit.script.exceptions.ParseException;
import webit.script.exceptions.ScriptRuntimeException;
import webit.script.io.Out;
import webit.script.io.impl.OutputStreamOut;
import webit.script.io.impl.WriterOut;
import webit.script.loaders.Resource;

/**
 *
 * @author Zqq
 */
public final class Template {

    public final Engine engine;
    public final String name;
    public final Resource resource;
    private TemplateAST templateAst;
    private long lastModified;
    //
    private final Object reloadLock = new Object();

    public Template(Engine engine, String name, Resource resource) {
        this.engine = engine;
        this.name = name;
        this.resource = resource;
    }

    /**
     *
     * @return TemplateAST
     * @throws IOException
     * @throws ParseException
     */
    public TemplateAST prepareTemplate() throws IOException, ParseException {
        TemplateAST tmpl;
        if ((tmpl = this.templateAst) == null || resource.isModified()) { //fast
            synchronized (reloadLock) {
                if ((tmpl = this.templateAst) == null || resource.isModified()) { //slow
                    this.templateAst = tmpl = new Parser().parseTemplate(
                            resource.openReader(), //Parser will close reader when finish
                            this);
                    lastModified = System.currentTimeMillis();
                }
            }
        }
        return tmpl;
    }

    /**
     *
     * @param root
     * @param outputStream
     * @return Context
     * @throws ScriptRuntimeException
     * @throws ParseException
     */
    public Context merge(final Map<String, Object> root, final OutputStream outputStream) throws ScriptRuntimeException, ParseException {
        return merge(root, new OutputStreamOut(outputStream, engine.getEncoding(), engine.getCoderFactory()));
    }

    /**
     *
     * @param root
     * @param outputStream
     * @param encoding
     * @return Context
     * @throws ScriptRuntimeException
     * @throws ParseException
     */
    public Context merge(final Map<String, Object> root, final OutputStream outputStream, final String encoding) throws ScriptRuntimeException, ParseException {
        return merge(root, new OutputStreamOut(outputStream, encoding != null ? encoding.toUpperCase().intern() : engine.getEncoding(), engine.getCoderFactory()));
    }

    /**
     *
     * @param root
     * @param writer
     * @return Context
     * @throws ScriptRuntimeException
     * @throws ParseException
     */
    public Context merge(final Map<String, Object> root, final Writer writer) throws ScriptRuntimeException, ParseException {
        return merge(root, new WriterOut(writer, engine.getEncoding(), engine.getCoderFactory()));
    }

    /**
     *
     * @param root
     * @param out
     * @return Context
     * @throws ScriptRuntimeException
     * @throws ParseException
     */
    public Context merge(final Map<String, Object> root, final Out out) throws ScriptRuntimeException, ParseException {
        try {
            final Context context;
            prepareTemplate().execute((context = new Context(this, out)), root);
            return context;
        } catch (Throwable e) {
            if (e instanceof ScriptRuntimeException) {
                throw ((ScriptRuntimeException) e).setTemplate(this);
            } else if (e instanceof ParseException) {
                throw ((ParseException) e).registTemplate(this);
            } else {
                throw new ScriptRuntimeException(e).setTemplate(this);
            }
        }
    }

    public void reset() {
        templateAst = null;
        lastModified = 0;
    }

    public long getLastModified() {
        return lastModified;
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Template other = (Template) obj;
        return (this.engine == other.engine) && (this.name.equals(other.name));
    }
}
