// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.test.tmpls;

import org.junit.Test;
import webit.script.EngineManager;
import webit.script.Template;
import webit.script.test.util.DiscardOutputStream;

/**
 *
 * @author Zqq
 */
public class HelloTest {

    @Test
    public void test() {
        try {
            Template template = EngineManager.getTemplate("/helloTest.wit");
            //template.reloadForce();
            template.merge(new DiscardOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
