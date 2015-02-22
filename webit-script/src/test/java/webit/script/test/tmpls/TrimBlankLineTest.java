// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.test.tmpls;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
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
public class TrimBlankLineTest {

    @Test
    public void test() throws ResourceNotFoundException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Engine engine = EngineManager.getEngine();
        DiscardOut out = new DiscardOut();

        Field field = Engine.class.getDeclaredField("trimCodeBlockBlankLine");
        ClassUtil.setAccessible(field);

        field.set(engine, true);
        Template template = engine.getTemplate("/trimBlankLine.wit");

        Map<String, Object> param = new HashMap<String, Object>(4);
        param.put("trimBlankLine", true);
        template.merge(KeyValuesUtil.wrap(param), out);

        field.set(engine, false);
        template.reset();

        param.put("trimBlankLine", false);
        template.merge(KeyValuesUtil.wrap(param), out);

    }
}
