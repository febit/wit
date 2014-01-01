// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script;

import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import webit.script.exceptions.ResourceNotFoundException;
import webit.script.exceptions.ScriptRuntimeException;
import webit.script.test.util.DiscardWriter;

/**
 *
 * @author Zqq
 */
public class TrimBlankLineTest {

    @Test
    public void test() throws ResourceNotFoundException {
        Engine engine = EngineManager.getEngine();
        DiscardWriter out = new DiscardWriter();

        engine.setTrimCodeBlockBlankLine(true);
        Template template = engine.getTemplate("/trimBlankLine.wit");

        try {

            Map<String, Object> param = new HashMap<String, Object>(4);
            param.put("trimBlankLine", true);
            template.merge(param, out);

            engine.setTrimCodeBlockBlankLine(false);
            template.reset();

            param.put("trimBlankLine", false);
            template.merge(param, out);
        } catch (ScriptRuntimeException e) {
            e.printStackTrace();
        }

    }
}
