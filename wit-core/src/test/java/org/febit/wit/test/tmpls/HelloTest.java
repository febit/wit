// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.test.tmpls;

import org.febit.wit.EngineManager;
import org.febit.wit.Template;
import org.febit.wit.exceptions.ResourceNotFoundException;
import org.febit.wit.io.impl.DiscardOut;
import org.junit.Test;

/**
 *
 * @author zqq90
 */
public class HelloTest {

    @Test
    public void test() throws ResourceNotFoundException {

        Template template = EngineManager.getTemplate("/helloTest.wit");
        template.reload();
        template.merge(new DiscardOut());
//        template.merge(System.out);
    }
}
