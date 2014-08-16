/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webit.script.test.tmpls;

import java.io.StringWriter;
import org.junit.Assert;
import org.junit.Test;
import webit.script.Engine;
import webit.script.exceptions.ResourceNotFoundException;

/**
 *
 * @author Zqq
 */
public class StringLoaderTest {

    @Test
    public void test() throws ResourceNotFoundException {

        final StringWriter writer = new StringWriter();

        Engine.createEngine("")
                .getTemplate("string:<% echo \"Hello Webit Script！\"; %>")
                .merge(writer);
        Assert.assertEquals("Hello Webit Script！", writer.toString());
    }
}
