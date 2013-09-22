// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script;

import java.io.File;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.junit.Test;
import webit.script.exceptions.ResourceNotFoundException;
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
                if (StringUtil.endsWithIgnoreCase(path, ".wtl")) {
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

        for (Iterator<String> it = templates.iterator(); it.hasNext();) {
            String templatePath = it.next();
            DiscardOutputStream out = new DiscardOutputStream();

            System.out.println("AUTO RUN: " + templatePath);
            Template template = engine.getTemplate(templatePath);

            template.merge(null, out);
        }
    }
}
