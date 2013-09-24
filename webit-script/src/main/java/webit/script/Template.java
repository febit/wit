// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Map;
import webit.script.core.Parser;
import webit.script.core.ast.TemplateAST;
import webit.script.exceptions.ParserException;
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
     * @throws ParserException
     */
    private TemplateAST prepareTemplate() throws IOException, ParserException {
        TemplateAST tmpl = this.templateAst;
        if (tmpl == null || resource.isModified()) { //fast
            synchronized (reloadLock) {
                tmpl = this.templateAst;
                if (tmpl == null || resource.isModified()) { //slow
                    tmpl = new Parser().parseTemplate(
                            resource.openReader(), //Parser will close reader when finish
                            this);
                    lastModified = System.currentTimeMillis();
                    this.templateAst = tmpl;
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
     * @throws ParserException
     */
    public Context merge(final Map<String, Object> root, final OutputStream outputStream) throws ScriptRuntimeException, ParserException {
        return merge(root, new OutputStreamOut(outputStream, engine.getEncoding(), engine.getCoderFactory()));
    }

    /**
     *
     * @param root
     * @param outputStream
     * @param encoding
     * @return Context
     * @throws ScriptRuntimeException
     * @throws ParserException
     */
    public Context merge(final Map<String, Object> root, final OutputStream outputStream, final String encoding) throws ScriptRuntimeException, ParserException {
        return merge(root, new OutputStreamOut(outputStream, encoding != null ? encoding.toUpperCase().intern() : engine.getEncoding(), engine.getCoderFactory()));
    }

    /**
     *
     * @param root
     * @param writer
     * @return Context
     * @throws ScriptRuntimeException
     * @throws ParserException
     */
    public Context merge(final Map<String, Object> root, final Writer writer) throws ScriptRuntimeException, ParserException {
        return merge(root, new WriterOut(writer, engine.getEncoding(), engine.getCoderFactory()));
    }

    /**
     *
     * @param root
     * @param out
     * @return Context
     * @throws ScriptRuntimeException
     * @throws ParserException
     */
    public Context merge(final Map<String, Object> root, final Out out) throws ScriptRuntimeException, ParserException {
        try {
            final TemplateAST tmpl = prepareTemplate();
            final Context context = new Context(this, out);
            tmpl.execute(context, root);
            return context;
        } catch (Throwable e) {
            if (e instanceof ScriptRuntimeException) {
                ((ScriptRuntimeException) e).setTemplate(this);
                throw (ScriptRuntimeException) e;
            } else if (e instanceof ParserException) {
                ((ParserException) e).registTemplate(this);
                throw (ParserException) e;
            }
            throw new ScriptRuntimeException(e).setTemplate(this);
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
