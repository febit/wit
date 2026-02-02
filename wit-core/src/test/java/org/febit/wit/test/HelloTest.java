// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.test;

import org.febit.wit.EngineManager;
import org.febit.wit.Template;
import org.febit.wit.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Test;

class HelloTest {

    @Test
    void test() throws ResourceNotFoundException {

        Template template = EngineManager.template("/helloTest.wit");
        template.reload();
        template.merge();
//        template.merge(System.out);
    }
}
