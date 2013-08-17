// Copyright (c) 2013, Webit Team. All Rights Reserved.

package webit.script;

import jodd.io.StringOutputStream;
import org.junit.Test;

/**
 *
 * @author Zqq
 */
public class ParserTest {

    @Test
    public void test() {
        
        Engine engine = EngineManager.getEngine();

        try {
            StringOutputStream out = new StringOutputStream();
        
            Template template = engine.getTemplate("/firstTmpl.wtl");
            //TemplateAST result = template.prepareTemplate();
            template.merge(null, out);
            System.out.println("===========================>>");
            System.out.println(out);
            System.out.println("<<===========================");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
