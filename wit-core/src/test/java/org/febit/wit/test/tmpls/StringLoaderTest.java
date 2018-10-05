// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.test.tmpls;

import org.febit.wit.EngineManager;
import org.febit.wit.exceptions.ResourceNotFoundException;
import org.junit.Assert;
import org.junit.Test;

import java.io.StringWriter;

/**
 * @author zqq90
 */
public class StringLoaderTest {

    @Test
    public void test() throws ResourceNotFoundException {

        final StringWriter writer = new StringWriter();

        EngineManager.getTemplate("string:<% echo \"Hello Wit！\"; %>")
                .merge(writer);
        Assert.assertEquals("Hello Wit！", writer.toString());
    }

    @Test
    public void testCodeFirst() throws ResourceNotFoundException {

        final StringWriter writer = new StringWriter();

        EngineManager.getTemplate("code: echo \"Hello Wit！\";")
                .merge(writer);
        Assert.assertEquals("Hello Wit！", writer.toString());
    }
}
