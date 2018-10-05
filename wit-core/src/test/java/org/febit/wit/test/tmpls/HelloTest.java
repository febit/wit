// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.test.tmpls;

import org.febit.wit.EngineManager;
import org.febit.wit.Template;
import org.febit.wit.exceptions.ResourceNotFoundException;
import org.junit.Test;

/**
 * @author zqq90
 */
public class HelloTest {

    @Test
    public void test() throws ResourceNotFoundException {

        Template template = EngineManager.getTemplate("/helloTest.wit");
        template.reload();
        template.merge();
//        template.merge(System.out);
    }
}
