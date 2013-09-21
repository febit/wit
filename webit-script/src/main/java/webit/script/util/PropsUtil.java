// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import jodd.props.Props;

/**
 *
 * @author zqq90
 */
public class PropsUtil {

    public static List<String> loadFromClasspath(final Props p, final String... pathSets) {
        final List<String> files = new LinkedList<String>();


        if (pathSets != null) {

            String pathSet;
            String[] paths;
            String path;
            ClassLoader classLoader = ClassLoaderUtil.getDefaultClassLoader();

            for (int i = 0; i < pathSets.length; i++) {
                pathSet = pathSets[i];
                if (pathSet != null && pathSet.length() > 0) {
                    paths = StringUtil.splitc(pathSet, ',');
                    StringUtil.trimAll(paths);
                    for (int j = 0; j < paths.length; j++) {
                        path = paths[j];
                        if (path != null && path.length() > 0) {
                            //load from classpath
                            if (path.charAt(0) == '/') {
                                path = path.substring(1);
                            }
                            InputStream in = classLoader.getResourceAsStream(path);
                            if (in != null) {
                                try {
                                    p.load(in, StringUtil.endsWithIgnoreCase(path, ".properties")
                                            ? "ISO-8859-1"
                                            : "UTF-8");
                                    files.add(path);
                                } catch (IOException ex) {
                                    //XXX:ignore
                                }
                            }//XXX: else ignore
                        }
                    }
                }
            }
        }
        return files;
    }
}
