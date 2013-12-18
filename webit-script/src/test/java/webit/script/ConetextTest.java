// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script;

import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.*;
import org.junit.Test;
import webit.script.exceptions.ResourceNotFoundException;
import webit.script.test.util.DiscardOutputStream;

/**
 *
 * @author Zqq
 */
public class ConetextTest {
    @Test
    public void test() throws ResourceNotFoundException {

        Template template = EngineManager.getTemplate("contextTest.wit");
        
        Context context = template.merge(new DiscardOutputStream());
        
        Map map = new HashMap();
        
        assertEquals("a", context.vars.get("a"));

        context.exportTo(map);
        assertTrue(map.containsKey("a"));
        assertTrue(map.containsKey("b"));
        assertFalse(map.containsKey("c"));
    }
}
