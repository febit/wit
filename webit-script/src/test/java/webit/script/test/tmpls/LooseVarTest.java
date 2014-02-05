// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.test.tmpls;

import org.junit.Test;
import webit.script.Engine;
import webit.script.EngineManager;
import webit.script.Template;
import webit.script.exceptions.ResourceNotFoundException;
import webit.script.io.impl.DiscardOut;

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
            
            Template template = engine.getTemplate("/looseVar.wit");
            template.merge(new DiscardOut());
        } finally {
            engine.setLooseVar(false);
        }
    }
}
