// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;
import webit.script.util.props.Props;

/**
 *
 * @author zqq90
 */
public class PropsUtil {

    private final static int BUFFER_SIZE = 3072;

    public static List<String> loadFromClasspath(final Props p, final String... pathSets) {
        final List<String> files = new LinkedList<String>();


        if (pathSets != null) {

            String pathSet;
            String[] paths;
            String path;
            
            ClassLoader classLoader = ClassLoaderUtil.getDefaultClassLoader();

            FastCharBuffer charsBuffer = new FastCharBuffer();
            Reader reader;
            InputStream in;
            char[] buffer = new char[BUFFER_SIZE];
            int read;

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
                            if ((in = classLoader.getResourceAsStream(path)) != null) {
                                try {

                                    reader = new InputStreamReader(in, StringUtil.endsWithIgnoreCase(path, ".properties")
                                            ? StringPool.ISO_8859_1
                                            : StringPool.UTF_8);

                                    charsBuffer.clear();

                                    while ((read = reader.read(buffer, 0, BUFFER_SIZE)) >= 0) {
                                        charsBuffer.append(buffer, 0, read);
                                    }

                                    p.load(charsBuffer.toString());
                                    files.add(path);
                                } catch (IOException ignore) {
                                    //XXX:ignore
                                } finally {
                                    try {
                                        in.close();
                                    } catch (Exception ignore) {
                                        //XXX:ignore
                                    }
                                    charsBuffer.clear();
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
