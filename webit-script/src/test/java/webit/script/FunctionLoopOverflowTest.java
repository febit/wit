// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
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
        template = EngineManager.getTemplate("/function/loopOverflow1.wit");
        try {
            template.reloadTemplateForce();
        } catch (ParseException e) {
            exception = e;
        }
        assertNotNull(exception);

        //
        exception = null;
        template = EngineManager.getTemplate("/function/loopOverflow2.wit");
        try {
            template.reloadTemplateForce();
        } catch (ParseException e) {
            exception = e;
        }
        assertNotNull(exception);

        //
        exception = null;
        template = EngineManager.getTemplate("/function/loopOverflow3.wit");
        try {
            template.reloadTemplateForce();
        } catch (ParseException e) {
            exception = e;
        }
        assertNotNull(exception);


    }
}
