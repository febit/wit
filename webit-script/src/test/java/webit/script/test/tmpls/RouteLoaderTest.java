// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.test.tmpls;

import org.junit.Test;
import webit.script.EngineManager;
import webit.script.exceptions.ResourceNotFoundException;
import webit.script.io.impl.DiscardOut;

/**
 *
 * @author zqq90
 */
public class RouteLoaderTest {

    @Test
    public void test() throws ResourceNotFoundException {

        DiscardOut out = new DiscardOut();

        EngineManager.getTemplate("lib:/lib.wit").merge(out);
        EngineManager.getTemplate("lib:sub:/lib-sub.wit").merge(out);
    }
}
