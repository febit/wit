// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.test.tmpls;

import org.febit.wit.Engine;
import org.febit.wit.EngineManager;
import org.febit.wit.Template;
import org.febit.wit.exceptions.ResourceNotFoundException;
import org.febit.wit.util.ClassUtil;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

/**
 * @author zqq90
 */
class LooseVarTest {

    @Test
    void test() throws ResourceNotFoundException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Engine engine = EngineManager.getEngine();
        Field field = Engine.class.getDeclaredField("looseVar");
        ClassUtil.setAccessible(field);
        try {
            field.set(engine, true);
            Template template = engine.getTemplate("/looseVar.wit");
            template.merge();
        } finally {
            field.set(engine, false);
        }
    }
}
