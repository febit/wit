// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit;

import org.febit.wit.exceptions.ResourceNotFoundException;

import java.io.IOException;

/**
 * @author zqq90
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

        TestForVisualVM.Start.start();
        for (int i = 0; i < times; i++) {
            //template.reset();
            //template.reload();
            template.merge();
        }
    }
}
