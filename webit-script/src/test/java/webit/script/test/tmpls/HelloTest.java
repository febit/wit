// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.test.tmpls;

import org.junit.Test;
import webit.script.EngineManager;
import webit.script.Template;
import webit.script.exceptions.ResourceNotFoundException;
import webit.script.io.impl.DiscardOut;

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
