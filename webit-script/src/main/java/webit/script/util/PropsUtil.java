// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.util;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import jodd.JoddCore;
import jodd.io.findfile.ClassScanner;
import jodd.props.Props;
import jodd.util.StringPool;
import jodd.util.StringUtil;

/**
 *
 * @author zqq90
 */
public class PropsUtil {

    public static List<String> loadFromClasspath(final Props p, final String... patterns) {
        final List<String> files = new LinkedList<String>();
        final ClassScanner scanner = new ClassScanner() {
            @Override
            protected void onEntry(EntryData entryData) throws IOException {
                String encoding = JoddCore.encoding;
                if (StringUtil.endsWithIgnoreCase(entryData.getName(), ".properties")) {
                    encoding = StringPool.ISO_8859_1;
                }
                p.load(entryData.openInputStream(), encoding);
                files.add(entryData.isArchive() ? entryData.getName() + '@' + entryData.getArchiveName() : entryData.getName());
            }
        };
        scanner.setIncludeResources(true);
        scanner.setIgnoreException(true);
        scanner.setIncludedEntries(patterns);
        scanner.scanDefaultClasspath();

        return files;
    }
}
