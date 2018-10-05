// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.test.tmpls;

import org.febit.wit.EngineManager;
import org.febit.wit.Template;
import org.febit.wit.exceptions.ParseException;
import org.febit.wit.exceptions.ResourceNotFoundException;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;

/**
 * @author zqq90
 */
public class FunctionLoopOverflowTest {

    @Test
    public void test() throws ResourceNotFoundException, IOException {
        Template template;
        ParseException exception;
        //
        exception = null;
        template = EngineManager.getTemplate("/loopTests/loopOverflow1.wit");
        try {
            template.reload();
        } catch (ParseException e) {
            exception = e;
        }
        assertNotNull(exception);

        //
        exception = null;
        template = EngineManager.getTemplate("/loopTests/loopOverflow2.wit");
        try {
            template.reload();
        } catch (ParseException e) {
            exception = e;
        }
        assertNotNull(exception);

        //
        exception = null;
        template = EngineManager.getTemplate("/loopTests/loopOverflow3.wit");
        try {
            template.reload();
        } catch (ParseException e) {
            exception = e;
        }
        assertNotNull(exception);

    }
}
