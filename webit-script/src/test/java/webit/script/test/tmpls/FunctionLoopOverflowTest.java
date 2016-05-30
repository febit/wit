// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.test.tmpls;

import java.io.IOException;
import static org.junit.Assert.*;
import org.junit.Test;
import webit.script.EngineManager;
import webit.script.Template;
import webit.script.exceptions.ParseException;
import webit.script.exceptions.ResourceNotFoundException;

/**
 *
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
