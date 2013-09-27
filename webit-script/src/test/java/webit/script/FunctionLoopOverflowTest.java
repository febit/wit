// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script;

import java.io.IOException;
import static org.junit.Assert.*;
import org.junit.Test;
import webit.script.exceptions.ParseException;
import webit.script.exceptions.ResourceNotFoundException;

/**
 *
 * @author Zqq
 */
public class FunctionLoopOverflowTest {

    @Test
    public void test() throws ResourceNotFoundException, IOException {
        Template template;
        ParseException exception;
        //
        exception = null;
        template = EngineManager.getTemplate("/function/loopOverflow1.wtl");
        try {
            template.prepareTemplate();
        } catch (ParseException e) {
            exception = e;
        }
        assertNotNull(exception);

        //
        exception = null;
        template = EngineManager.getTemplate("/function/loopOverflow2.wtl");
        try {
            template.prepareTemplate();
        } catch (ParseException e) {
            exception = e;
        }
        assertNotNull(exception);

        //
        exception = null;
        template = EngineManager.getTemplate("/function/loopOverflow3.wtl");
        try {
            template.prepareTemplate();
        } catch (ParseException e) {
            exception = e;
        }
        assertNotNull(exception);


    }
}
