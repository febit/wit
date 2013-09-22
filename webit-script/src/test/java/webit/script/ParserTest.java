// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script;

import org.junit.Test;
import webit.script.test.util.DiscardOutputStream;

/**
 *
 * @author Zqq
 */
public class ParserTest {

    @Test
    public void test() {
        
        Engine engine = EngineManager.getEngine();

        try {
            DiscardOutputStream out = new DiscardOutputStream();
            Template template = engine.getTemplate("/firstTmpl.wtl");
            //TemplateAST result = template.prepareTemplate();
            template.merge(null, out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
