// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script;

import java.io.OutputStream;
import org.junit.Test;
import webit.script.exceptions.ResourceNotFoundException;
import webit.script.test.util.DiscardOutputStream;

/**
 *
 * @author zqq90
 */
public class RouteLoaderTest {

    @Test
    public void test() throws ResourceNotFoundException {

        OutputStream out = new DiscardOutputStream();
        
        EngineManager.getTemplate("lib:/lib.wit").merge(out);
        EngineManager.getTemplate("lib:sub:/lib-sub.wit").merge(out);
    }
}
