// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.test.feature;

import org.febit.wit.EngineManager;
import org.febit.wit.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Test;

import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;

class StringLoaderTest {

    @Test
    void test() throws ResourceNotFoundException {

        final StringWriter writer = new StringWriter();

        EngineManager.template("string:<% echo \"Hello Wit！\"; %>")
                .merge(writer);
        assertEquals("Hello Wit！", writer.toString());
    }

    @Test
    void testCodeFirst() throws ResourceNotFoundException {

        final StringWriter writer = new StringWriter();

        EngineManager.template("code: echo \"Hello Wit！\";")
                .merge(writer);
        assertEquals("Hello Wit！", writer.toString());
    }
}
