// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script;

import org.junit.Test;
import webit.script.test.util.DiscardOutputStream;

/**
 *
 * @author Zqq
 */
public class TestForVisualVM {

    public static class Start {

        public static void start() {
        }
    }

    //@Test
    public void test() {
        int times = 10000;
        Engine engine = EngineManager.getEngine();

        try {
            Template template = engine.getTemplate("/firstTmpl.wtl");
            DiscardOutputStream out = new DiscardOutputStream();
            template.prepareTemplate();
            
            //webit.script.TestForVisualVM.Start
            TestForVisualVM.Start.start();
            for (int i = 0; i < times; i++) {
                //template.reset();
                //template.prepareTemplate();
                template.merge(null, out);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
