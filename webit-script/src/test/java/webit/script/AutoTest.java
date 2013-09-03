// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import jodd.io.StringOutputStream;
import jodd.io.findfile.ClassFinder;
import jodd.io.findfile.ClassScanner;
import jodd.util.StringUtil;
import org.junit.Test;
import webit.script.exceptions.ResourceNotFoundException;

/**
 *
 * @author Zqq
 */
public class AutoTest {

    private final static String AUTO_TEST_PATH = "/webit/script/test/tmpls/auto/*";
    private final static String TEMPLATE_ROOT = "/webit/script/test/tmpls";

    private List<String> collectAutoTestTemplates() {
        final List<String> templates = new ArrayList<String>();
        final ClassScanner scanner = new ClassScanner() {
            @Override
            protected void onEntry(ClassFinder.EntryData entryData) throws IOException {
                if (StringUtil.endsWithIgnoreCase(entryData.getName(), ".wtl")) {
                    templates.add(StringUtil.cutPrefix(entryData.getName(), TEMPLATE_ROOT));
                }
            }
        };
        scanner.setIncludeResources(true);
        scanner.setIgnoreException(true);
        scanner.setIncludedEntries(AUTO_TEST_PATH);
        scanner.scanDefaultClasspath();

        return templates;
    }

    @Test
    public void test() throws ResourceNotFoundException {

        Engine engine = EngineManager.getEngine();
        List<String> templates = collectAutoTestTemplates();

        for (Iterator<String> it = templates.iterator(); it.hasNext();) {
            String templatePath = it.next();
            StringOutputStream out = new StringOutputStream();

            System.out.println("AUTO RUN: "+templatePath);
            Template template = engine.getTemplate(templatePath);

            template.merge(null, out);
        }
    }
}
