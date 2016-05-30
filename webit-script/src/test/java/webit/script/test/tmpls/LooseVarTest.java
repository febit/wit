// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.test.tmpls;

import java.lang.reflect.Field;
import org.junit.Test;
import webit.script.Engine;
import webit.script.EngineManager;
import webit.script.Template;
import webit.script.exceptions.ResourceNotFoundException;
import webit.script.io.impl.DiscardOut;
import webit.script.util.ClassUtil;

/**
 *
 * @author zqq90
 */
public class LooseVarTest {

    @Test
    public void test() throws ResourceNotFoundException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Engine engine = EngineManager.getEngine();
        Field field = Engine.class.getDeclaredField("looseVar");
        ClassUtil.setAccessible(field);
        try {
            field.set(engine, true);
            Template template = engine.getTemplate("/looseVar.wit");
            template.merge(new DiscardOut());
        } finally {
            field.set(engine, false);
        }
    }
}
