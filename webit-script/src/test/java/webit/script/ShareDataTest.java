// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script;

import org.junit.Test;
import webit.script.exceptions.ResourceNotFoundException;
import webit.script.test.util.DiscardOutputStream;
import webit.script.util.keyvalues.KeyValuesUtil;

/**
 *
 * @author Zqq
 */
public class ShareDataTest {

    @Test
    public void test() throws ResourceNotFoundException {
        Engine engine = EngineManager.getEngine();
        try {
            engine.setShareRootData(true);

            Template template = engine.getTemplate("/shareData.wit");
            template.reloadTemplateForce();
            template.merge(KeyValuesUtil.wrap(new String[]{"v1", "v2"}, new Object[]{"V1", "V2"}), new DiscardOutputStream());
        } finally {
            engine.setShareRootData(false);
        }
    }
}
