// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.test.tmpls;

import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.*;
import org.junit.Test;
import webit.script.Context;
import webit.script.EngineManager;
import webit.script.Template;
import webit.script.exceptions.ResourceNotFoundException;
import webit.script.io.impl.DiscardOut;

/**
 *
 * @author zqq90
 */
public class ConetextTest {

    @Test
    public void test() throws ResourceNotFoundException {

        Template template = EngineManager.getTemplate("contextTest.wit");

        Context context = template.merge(new DiscardOut());

        Map map = new HashMap();

        assertEquals("a", context.get("a"));

        context.exportTo(map);
        assertTrue(map.containsKey("a"));
        assertTrue(map.containsKey("b"));
        assertFalse(map.containsKey("c"));

        assertEquals("a", map.get("a"));
        assertEquals("b", map.get("b"));
    }
}
