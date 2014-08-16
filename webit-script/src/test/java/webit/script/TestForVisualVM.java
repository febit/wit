// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script;

import java.io.IOException;
import webit.script.exceptions.ResourceNotFoundException;
import webit.script.io.impl.DiscardOut;

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
    public void test() throws ResourceNotFoundException, IOException {
        int times = 10000;
        Engine engine = EngineManager.getEngine();

        Template template = engine.getTemplate("/helloTest.wit");
        DiscardOut out = new DiscardOut();

        TestForVisualVM.Start.start();
        for (int i = 0; i < times; i++) {
            //template.reset();
            //template.reload();
            template.merge(out);
        }
    }
}
