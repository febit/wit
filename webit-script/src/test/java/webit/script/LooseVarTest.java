// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script;

import org.junit.Test;
import webit.script.exceptions.ResourceNotFoundException;
import webit.script.test.util.DiscardOutputStream;

/**
 *
 * @author Zqq
 */
public class LooseVarTest {

    @Test
    public void test() throws ResourceNotFoundException {
        Engine engine = EngineManager.getEngine();
        try {
            engine.setLooseVar(true);
            
            Template template = engine.getTemplate("/looseVar.wtl");
            template.merge(null, new DiscardOutputStream());
        } finally {
            engine.setLooseVar(false);
        }
    }
}
