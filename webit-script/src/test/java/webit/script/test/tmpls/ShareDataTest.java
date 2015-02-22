// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.test.tmpls;

import java.lang.reflect.Field;
import org.junit.Test;
import webit.script.Engine;
import webit.script.EngineManager;
import webit.script.Template;
import webit.script.exceptions.ResourceNotFoundException;
import webit.script.io.impl.DiscardOut;
import webit.script.util.ClassUtil;
import webit.script.util.KeyValuesUtil;

/**
 *
 * @author Zqq
 */
public class ShareDataTest {

    @Test
    public void test() throws ResourceNotFoundException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Engine engine = EngineManager.getEngine();
        Field field = Engine.class.getDeclaredField("shareRootData");
        ClassUtil.setAccessible(field);
        try {
            field.set(engine, true);
            Template template = engine.getTemplate("/shareData.wit");
            template.reload();
            template.merge(KeyValuesUtil.wrap(new String[]{"v1", "v2"}, new Object[]{"V1", "V2"}), new DiscardOut());
        } finally {
            field.set(engine, true);
        }
    }
}
