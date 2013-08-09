package webit.tl;

import webit.script.Engine;
import webit.script.Template;
import jodd.io.StringOutputStream;
import org.junit.Test;
import webit.script.core.ast.TemplateAST;

/**
 *
 * @author Zqq
 */
public class ParserTest {

    @Test
    public void test() {
      
        Engine engine = Engine.getEngine("/webitl-test1.props", null);
        //engine.setEncoding("UTF-8");
        //ClasspathLoader classpathLoader = new ClasspathLoader();
        //classpathLoader.setRoot("webit/tl/test/tmpls");
        //classpathLoader.setEncoding("UTF-8");
        
        //engine.setResourceLoader(classpathLoader);
        
        try {
            //TemplateAST result = p.parserTemplate(new java.io.FileReader("F:\\Workspace\\webitl\\src\\main\\resources\\webit\\tl\\test\\tmpls\\firstTmpl.wtl"));
            //System.out.println(result);
            StringOutputStream out = new StringOutputStream();
            //Template template = engine.getTemplate("webit/tl/test/tmpls/firstTmpl.wtl");
            Template template = engine.getTemplate("/webit/tl/test/tmpls/firstTmpl.wtl");
            //
            //TemplateAST result = template.prepareTemplate();
            template.execute(null, out);
            System.out.println("===========================>>");
            System.out.println(out);
            System.out.println("<<===========================");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
