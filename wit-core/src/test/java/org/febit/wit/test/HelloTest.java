// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.test;

import org.febit.wit.EngineManager;
import org.febit.wit.exception.SourceNotFoundException;
import org.junit.jupiter.api.Test;

class HelloTest {

    @Test
    void test() throws SourceNotFoundException {

        var script = EngineManager.script("/helloTest.wit");
        script.reload();
        script.eval();
//        script.eval(System.out);
    }
}
