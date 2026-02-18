// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.test.feature;

import org.febit.wit.EngineManager;
import org.febit.wit.Vars;
import org.febit.wit.exception.SourceNotFoundException;
import org.junit.jupiter.api.Test;

import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;

class StringLoaderTest {

    @Test
    void test() throws SourceNotFoundException {
        var writer = new StringWriter();

        EngineManager.script("string:<% echo \"Hello Wit！\"; %>")
                .eval(Vars.empty(), writer);
        assertEquals("Hello Wit！", writer.toString());
    }

    @Test
    void testCodeFirst() throws SourceNotFoundException {
        var writer = new StringWriter();

        EngineManager.script("code: echo \"Hello Wit！\";")
                .eval(Vars.empty(), writer);
        assertEquals("Hello Wit！", writer.toString());
    }
}
