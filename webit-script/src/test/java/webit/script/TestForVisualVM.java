// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script;

import org.junit.Test;
import webit.script.test.util.DiscardOutputStream;

/**
 *
 * @author Zqq
 */
public class TestForVisualVM {

    @Test
    public void test() {
        int times = 10000;
        Engine engine = EngineManager.getEngine();

        try {
            Template template = engine.getTemplate("/firstTmpl.wtl");
            template.prepareTemplate();
            DiscardOutputStream out = new DiscardOutputStream();
            //TemplateAST result = template.prepareTemplate();
            for (int i = 0; i < times; i++) {
                
            //template.prepareTemplate();
                template.merge(null, out);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
