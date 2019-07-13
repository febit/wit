// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.test.tmpls;

import org.febit.wit.Context;
import org.febit.wit.EngineManager;
import org.febit.wit.Function;
import org.febit.wit.exceptions.NotFunctionException;
import org.febit.wit.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Test;

import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author zqq90
 */
class FunctionExportTest {

    @Test
    void test() throws ResourceNotFoundException {

        Context context = EngineManager.getTemplate("/functionExportTest.wit").merge();

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

        // Exception cases:
        NotFunctionException exception;

        exception = assertThrows(NotFunctionException.class,
                () -> context.exportFunction("notExistFunction"));
        assertEquals("Not function: null", exception.getMessage());

        exception = assertThrows(NotFunctionException.class,
                () -> context.exportFunction("count"));
        assertEquals("Not function: java.lang.Integer", exception.getMessage());
    }
}
