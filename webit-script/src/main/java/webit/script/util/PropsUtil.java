// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import webit.script.CFG;

/**
 *
 * @author zqq90
 */
public class PropsUtil {

    public static final ClasspathInputResolver CLASSPATH_INPUT_RESOLVER = new ClasspathInputResolver();

    public static Props loadFromClasspath(final Props props, final String... pathSets) {
        return load(props, CLASSPATH_INPUT_RESOLVER, pathSets);
    }

    public static Props load(final Props props, final InputResolver inputResolver, final String... pathSets) {
        if (pathSets != null) {
            new PropsLoader(props).load(inputResolver, pathSets);
        }
        return props;
    }

    private static class PropsLoader {

        private final Props props;
        private final char[] _buffer;
        private final FastCharBuffer _charsBuffer;

        private Set<String> loadedModules;
        private Map<String, Props> modulePropsCache;

        PropsLoader(Props props) {
            this.props = props;
            this._buffer = new char[3072];
            this._charsBuffer = new FastCharBuffer();
        }

        private void mergeProps(Props src, String name) {
            this.props.merge(src);
            this.props.append(CFG.PROPS_FILE_LIST, name);
        }

        private void resolveModules(Props src) {
            resolveModules(src.remove("@modules"));
        }

        private void resolveModules(String modules) {
            if (modules == null) {
                return;
            }
            if (this.loadedModules == null) {
                this.loadedModules = new HashSet<String>();
            }
            if (this.modulePropsCache == null) {
                this.modulePropsCache = new HashMap<String, Props>();
            }
            for (String module : StringUtil.splitAndRemoveBlank(modules)) {
                if (loadedModules.contains(module)) {
                    continue;
                }
                Props moduleProps = modulePropsCache.get(module);
                if (moduleProps == null) {
                    moduleProps = loadProps(CLASSPATH_INPUT_RESOLVER, module);
                    modulePropsCache.put(module, moduleProps);
                    resolveModules(moduleProps);
                    if (loadedModules.contains(module)) {
                        //XXX: show warning: self depended!
                        continue;
                    }
                }
                loadedModules.add(module);
                mergeProps(moduleProps, CLASSPATH_INPUT_RESOLVER.getViewPath(module));
            }
        }

        private Props loadProps(InputResolver inputResolver, final String path) {
            final FastCharBuffer charsBuffer = this._charsBuffer;
            final char[] buffer = this._buffer;
            final InputStream in = inputResolver.openInputStream(path);
            Reader reader = null;
            if (in != null) {
                try {
                    reader = new InputStreamReader(in, "UTF-8");
                    charsBuffer.clear();
                    int read;
                    while ((read = reader.read(buffer)) >= 0) {
                        charsBuffer.append(buffer, 0, read);
                    }
                    final Props tempProps = new Props();
                    tempProps.load(charsBuffer.toArray());
                    charsBuffer.clear();
                    return tempProps;
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
            }
            throw new RuntimeException("Not found props: ".concat(inputResolver.getViewPath(path)));
        }

        void load(InputResolver inputResolver, final String... paths) {
            if (inputResolver instanceof ClasspathInputResolver) {
                for (String modules : paths) {
                    resolveModules(modules);
                }
            } else {
                for (String path : paths) {
                    for (String subpath : StringUtil.splitAndRemoveBlank(path)) {
                        Props temp = loadProps(inputResolver, subpath);
                        resolveModules(temp);
                        mergeProps(temp, inputResolver.getViewPath(subpath));
                    }
                }
            }
        }
    }

    public static interface InputResolver {

        InputStream openInputStream(String path);

        String getViewPath(String path);
    }

    public static class ClasspathInputResolver implements InputResolver {

        ClasspathInputResolver() {
        }

        public InputStream openInputStream(String path) {
            return ClassUtil.getDefaultClassLoader().getResourceAsStream(path.charAt(0) == '/'
                    ? path.substring(1)
                    : path);
        }

        public String getViewPath(String path) {
            return "%CLASS_PATH%/".concat(path.charAt(0) == '/'
                    ? path.substring(1)
                    : path);
        }
    }
}
