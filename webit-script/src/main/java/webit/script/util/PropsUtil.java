// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import webit.script.CFG;
import webit.script.util.props.Props;

/**
 *
 * @author zqq90
 */
public class PropsUtil {

    private final static int BUFFER_SIZE = 3072;
    private final static String CLASSPATH_PREFIX = "%CLASS_PATH%/";

    public static interface InputStreamResolver {

        InputStream openInputStream(String path);

        String getViewPath(String path);
    }

    public static Props loadFromClasspath(final Props props, final String... pathSets) {

        return load(props, new InputStreamResolver() {

            public InputStream openInputStream(String path) {
                return ClassLoaderUtil.getDefaultClassLoader().getResourceAsStream(path.charAt(0) == '/'
                        ? path.substring(1)
                        : path);
            }

            public String getViewPath(String path) {
                return CLASSPATH_PREFIX.concat(path.charAt(0) == '/'
                        ? path.substring(1)
                        : path);
            }
        }, pathSets);
    }

    public static Props load(final Props props, InputStreamResolver inputStreamResolver, final String... pathSets) {

        if (pathSets != null) {

            String pathSet;
            String[] paths;
            String path;

            final FastCharBuffer charsBuffer = new FastCharBuffer();
            final char[] buffer = new char[BUFFER_SIZE];

            Reader reader = null;
            InputStream in;
            int read;

            for (int i = 0, leni = pathSets.length; i < leni; i++) {
                if ((pathSet = pathSets[i]) != null && pathSet.length() != 0) {
                    StringUtil.trimAll(paths = StringUtil.splitc(pathSet, ','));
                    for (int j = 0, lenj = paths.length; j < lenj; j++) {
                        if ((path = paths[j]) != null && path.length() != 0) {
                            //load
                            if ((in = inputStreamResolver.openInputStream(path)) != null) {
                                try {

                                    reader = new InputStreamReader(in, StringUtil.endsWithIgnoreCase(path, ".properties")
                                            ? StringPool.ISO_8859_1
                                            : StringPool.UTF_8);

                                    charsBuffer.clear();

                                    while ((read = reader.read(buffer, 0, BUFFER_SIZE)) >= 0) {
                                        charsBuffer.append(buffer, 0, read);
                                    }

                                    props.load(charsBuffer.toArray());
                                    charsBuffer.clear();
                                    props.append(CFG.PROPS_FILE_LIST, inputStreamResolver.getViewPath(path));
                                } catch (IOException ignore) {
                                } finally {
                                    try {
                                        if (reader != null) {
                                            reader.close();
                                        } else {
                                            in.close();
                                        }
                                    } catch (IOException ignore) {
                                    }
                                }
                            }//Note: else ignore not found props
                        }
                    }
                }
            }
        }
        return props;
    }
}
