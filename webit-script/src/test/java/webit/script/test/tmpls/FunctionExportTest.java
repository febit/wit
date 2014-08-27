// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.test.tmpls;

import java.io.StringWriter;
import static org.junit.Assert.*;
import org.junit.Test;
import webit.script.Context;
import webit.script.EngineManager;
import webit.script.Function;
import webit.script.exceptions.NotFunctionException;
import webit.script.exceptions.ResourceNotFoundException;
import webit.script.io.impl.DiscardOut;

/**
 *
 * @author zqq
 */
public class FunctionExportTest {

    @Test
    public void test() throws ResourceNotFoundException {

        Context context = EngineManager.getTemplate("/functionExportTest.wit").merge(new DiscardOut());

        //plus
        Function plus = context.exportFunction("plus");

        assertNotNull(plus);

        assertEquals(1, plus.invoke(0, 1));
        assertEquals(5, plus.invoke(2, 3));
        assertEquals(1, plus.invoke(-2, 3));

        //counter
        Function counter = context.exportFunction("counter");

        assertNotNull(counter);

        assertEquals(0, counter.invoke());
        assertEquals(1, counter.invoke());
        assertEquals(2, counter.invoke());

        //counter
        Function counter2 = context.exportFunction("counter2");

        assertNotNull(counter2);

        assertEquals(0, counter2.invoke());
        assertEquals(1, counter2.invoke());
        assertEquals(2, counter2.invoke());

        //str_len
        Function str_len = context.exportFunction("str_len");

        assertNotNull(str_len);

        assertEquals(0, str_len.invoke(""));
        assertEquals(1, str_len.invoke("a"));
        assertEquals(4, str_len.invoke("abcd"));

        //print
        Function print = context.exportFunction("print");
        StringWriter writer;

        assertNotNull(print);

        writer = new StringWriter();
        print.invokeWithOut(writer, "");
        assertEquals("", writer.toString());
        
        writer = new StringWriter();
        print.invokeWithOut(writer, "hello function");
        assertEquals("hello function", writer.toString());

        //Exception cases:
        //
        Exception exception = null;
        try {
            context.exportFunction("notExistFunction");
        } catch (NotFunctionException e) {
            exception = e;
        }
        assertNotNull(exception);

        assertEquals("Not a function but a [null].", exception.getMessage());

        //
        exception = null;
        try {
            context.exportFunction("count");
        } catch (NotFunctionException e) {
            exception = e;
        }
        assertNotNull(exception);

        assertEquals("Not a function but a [class java.lang.Integer].", exception.getMessage());
    }
}
