// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.test;

import org.febit.wit.EngineManager;
import org.febit.wit.Script;
import org.febit.wit.exceptions.SourceNotFoundException;
import org.junit.jupiter.api.Test;

class HelloTest {

    @Test
    void test() throws SourceNotFoundException {

        Script script = EngineManager.script("/helloTest.wit");
        script.reload();
        script.merge();
//        script.merge(System.out);
    }
}
