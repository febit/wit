// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.test.tmpls;

import org.junit.Test;
import webit.script.EngineManager;
import webit.script.exceptions.ResourceNotFoundException;

/**
 *
 * @author zqq90
 */
public class AssistantSuffixsTest {

    @Test
    public void test() throws ResourceNotFoundException {
        EngineManager.getTemplate("/assistantSuffixsTest.whtml").reload();
        EngineManager.getTemplate("/assistantSuffixsTest.wit2").reload();
    }
}
