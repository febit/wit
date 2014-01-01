// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script;

import org.junit.Test;
import webit.script.test.util.DiscardOutputStream;

/**
 *
 * @author Zqq
 */
public class ParserTest {

    @Test
    public void test() {
        try {
            Template template = EngineManager.getTemplate("/firstTmpl.wit");
            //TemplateAST result = template.reloadTemplateForce();
            template.merge(new DiscardOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
