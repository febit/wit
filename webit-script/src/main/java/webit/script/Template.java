package webit.script;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.util.Map;
import webit.script.core.Parser;
import webit.script.core.ast.TemplateAST;
import webit.script.exceptions.ParserException;
import webit.script.exceptions.ScriptRuntimeException;
import webit.script.loaders.Resource;

/**
 *
 * @author Zqq
 */
public class Template {

    public final Engine engine;
    public final String name;
    public final Resource resource;
    private TemplateAST templateAst;
    private long lastModified;

    public Template(Engine engine, String name, Resource resource) {
        this.engine = engine;
        this.name = name;
        this.resource = resource;
    }

    protected TemplateAST prepareTemplate() throws IOException, ParserException{
        TemplateAST tmpl = this.templateAst;
        if (tmpl == null || resource.isModified()) { //fast
            synchronized (this) {
                tmpl = this.templateAst;
                if (tmpl == null || resource.isModified()) { //slow
                    Parser parser = new Parser();
                    Reader reader = resource.openReader();
                    tmpl = parser.parserTemplate(reader, this);
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
     * @param out
     * @return Context
     * @throws ScriptRuntimeException
     * @throws ParserException
     */
    public Context execute(Map<String, Object> root, OutputStream out) throws ScriptRuntimeException, ParserException{
        try {
            TemplateAST tmpl = prepareTemplate();
            Context context = new Context(this, out);
            tmpl.execute(context, root);
            return context;
        } catch (Exception e) {
            if (e instanceof ScriptRuntimeException) {
                ((ScriptRuntimeException)e).setTemplate(this);
                throw (ScriptRuntimeException)e;
            }else if (e instanceof ParserException) {
                ((ParserException)e).registTemplate(this);
                throw (ParserException)e;
            }
            throw new ScriptRuntimeException(e);
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
