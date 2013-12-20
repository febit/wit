// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script;

import java.io.File;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.junit.Test;
import webit.script.exceptions.ParseException;
import webit.script.exceptions.ResourceNotFoundException;
import webit.script.exceptions.ScriptRuntimeException;
import webit.script.global.AssertGlobalRegister;
import webit.script.test.util.DiscardOutputStream;
import webit.script.util.ClassLoaderUtil;
import webit.script.util.StringUtil;

/**
 *
 * @author Zqq
 */
public class AutoTest {

    private final static String AUTO_TEST_PATH = "webit/script/test/tmpls/auto/";

    private List<String> collectAutoTestTemplates() {
        final List<String> templates = new LinkedList<String>();

        ClassLoader classLoader = ClassLoaderUtil.getDefaultClassLoader();
        try {
            URL url = classLoader.getResource(AUTO_TEST_PATH);
            File file = new File(url.getFile());
            String[] files = file.list();
            for (int i = 0; i < files.length; i++) {
                String path = files[i];
                if (StringUtil.endsWithIgnoreCase(path, ".wit")) {
                    templates.add("/auto/" + path);
                }
            }

        } catch (Exception ex) {
            //ignore
        }
        return templates;
    }

    @Test
    public void test() throws ResourceNotFoundException {

        Engine engine = EngineManager.getEngine();
        List<String> templates = collectAutoTestTemplates();

        AssertGlobalRegister assertGlobalRegister = AssertGlobalRegister.instance;
        try {

            for (Iterator<String> it = templates.iterator(); it.hasNext();) {
                String templatePath = it.next();
                DiscardOutputStream out = new DiscardOutputStream();

                System.out.println("AUTO RUN: " + templatePath);
                Template template = engine.getTemplate(templatePath);
                assertGlobalRegister.resetAssertCount();
                template.merge(out);

                System.out.println("\tassert count: " + assertGlobalRegister.getAssertCount());
            }
        } catch (ParseException e) {
            e.printStackTrace();
            throw e;
        } catch (ScriptRuntimeException e) {
            e.printStackTrace();
            throw e;
        }
    }
}
