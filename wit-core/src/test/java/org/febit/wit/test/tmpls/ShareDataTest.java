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

/**
 * @author zqq90
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
            template.merge(Vars.of(new String[]{"v1", "v2"}, new Object[]{"V1", "V2"}));
        } finally {
            field.set(engine, true);
        }
    }
}
