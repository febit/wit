// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.test.tmpls;

import org.febit.wit.Engine;
import org.febit.wit.EngineManager;
import org.febit.wit.Template;
import org.febit.wit.Vars;
import org.febit.wit.exceptions.ResourceNotFoundException;
import org.febit.wit.util.ClassUtil;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zqq90
 */
public class TrimBlankLineTest {

    @Test
    public void test() throws ResourceNotFoundException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Engine engine = EngineManager.getEngine();

        Field field = Engine.class.getDeclaredField("trimCodeBlockBlankLine");
        ClassUtil.setAccessible(field);

        field.set(engine, true);
        Template template = engine.getTemplate("/trimBlankLine.wit");

        Map<String, Object> param = new HashMap<>(4);
        param.put("trimBlankLine", true);
        template.merge(Vars.of(param));

        field.set(engine, false);
        template.reset();

        param.put("trimBlankLine", false);
        template.merge(Vars.of(param));

    }
}
