// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script;

import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import webit.script.exceptions.ResourceNotFoundException;
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

        engine.setTrimTextStatmentRightBlankLine(true);
        Template template = engine.getTemplate("/trimBlankLine.wtl");

        Map<String, Object> param = new HashMap<String, Object>(4);
        param.put("trimBlankLine", true);
        template.merge(param, out);

        engine.setTrimTextStatmentRightBlankLine(false);
        template.reset();

        param.put("trimBlankLine", false);
        template.merge(param, out);

    }
}
