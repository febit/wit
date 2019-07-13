// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.test.tmpls;

import org.febit.wit.EngineManager;
import org.febit.wit.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Test;

import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author zqq90
 */
class StringLoaderTest {

    @Test
    void test() throws ResourceNotFoundException {

        final StringWriter writer = new StringWriter();

        EngineManager.getTemplate("string:<% echo \"Hello Wit！\"; %>")
                .merge(writer);
        assertEquals("Hello Wit！", writer.toString());
    }

    @Test
    void testCodeFirst() throws ResourceNotFoundException {

        final StringWriter writer = new StringWriter();

        EngineManager.getTemplate("code: echo \"Hello Wit！\";")
                .merge(writer);
        assertEquals("Hello Wit！", writer.toString());
    }
}
