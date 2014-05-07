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
import webit.script.util.props.Props;

/**
 *
 * @author zqq90
 */
public class PropsUtil {

    public static Props loadFromClasspath(final Props props, final String... pathSets) {
        return load(props, new ClasspathInputStreamResolver(), pathSets);
    }

    public static Props load(final Props props, InputStreamResolver inputStreamResolver, final String... pathSets) {
        new PropsLoader(inputStreamResolver, props).load(pathSets);
        return props;
    }

    private static class PropsLoader {

        private final static int BUFFER_SIZE = 3072;
        private final static String PROPS_MODULES = "@modules";

        private final InputStreamResolver mainInputStreamResolver;
        private final Props props;
        private final boolean isModuleMode;
        //
        private Set<String> loadedModules;
        private Map<String, Props> modulePropsCache;
        private InputStreamResolver moduleInputStreamResolver;
        //
        final FastCharBuffer _charsBuffer = new FastCharBuffer();
        final char[] _buffer = new char[BUFFER_SIZE];

        public PropsLoader(InputStreamResolver mainInputStreamResolver, Props props) {
            this.mainInputStreamResolver = mainInputStreamResolver;
            this.props = props;

            if ((this.isModuleMode = (mainInputStreamResolver instanceof ClasspathInputStreamResolver))) {
                this.moduleInputStreamResolver = mainInputStreamResolver;
            }
        }

        private void initForModules() {
            if (this.moduleInputStreamResolver == null) {
                this.moduleInputStreamResolver = new ClasspathInputStreamResolver();
            }
            if (this.loadedModules == null) {
                this.loadedModules = new HashSet<String>();
            }
            if (this.modulePropsCache == null) {
                this.modulePropsCache = new HashMap<String, Props>();
            }
        }

        private void logPropsFiles(String name) {
            this.props.append(CFG.PROPS_FILE_LIST, name);
        }

        private void resolveModules(String modules) {
            if (modules == null) {
                return;
            }
            this.initForModules();
            for (String module : StringUtil.splitAndTrimAll(modules)) {
                if (module.length() != 0) {
                    if (module.charAt(0) == '/') {
                        module = module.substring(1);
                    }
                    if (loadedModules.contains(module)) {
                        continue;
                    }
                    Props moduleProps;
                    if ((moduleProps = modulePropsCache.get(module)) == null) {
                        modulePropsCache.put(module,
                                moduleProps = loadProps(this.moduleInputStreamResolver, module));
                        //resolve PROPS_MODULES after loaded
                        if (moduleProps == null) {
                            throw new RuntimeException("Not found props named:" + module);
                        }
                        resolveModules(moduleProps.popBaseProperty(PROPS_MODULES));

                        if (loadedModules.contains(module)) {
                            //XXX: show warning: self depended!
                            continue;
                        }
                    }
                    loadedModules.add(module);
                    this.props.merge(moduleProps);
                    logPropsFiles(this.moduleInputStreamResolver.getViewPath(module));
                }
            }
        }

        private Props loadProps(InputStreamResolver inputStreamResolver, final String path) {

            final FastCharBuffer charsBuffer = this._charsBuffer;
            final char[] buffer = this._buffer;

            final InputStream in;
            Reader reader = null;
            if ((in = inputStreamResolver.openInputStream(path)) != null) {
                try {
                    reader = new InputStreamReader(in, StringUtil.endsWithIgnoreCase(path, ".properties")
                            ? StringPool.ISO_8859_1
                            : StringPool.UTF_8);

                    charsBuffer.clear();
                    int read;
                    while ((read = reader.read(buffer, 0, BUFFER_SIZE)) >= 0) {
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
            return null;
        }

        void load(final String... paths) {
            if (paths == null) {
                return;
            }
            if (this.isModuleMode) {
                for (String modules : paths) {
                    resolveModules(modules);
                }
            } else {
                for (String path : paths) {
                    if (path == null || (path = path.trim()).length() == 0) {
                        continue;
                    }
                    for (String subpath : StringUtil.splitAndTrimAll(path)) {
                        if (subpath.length() != 0) {
                            Props temp = loadProps(this.mainInputStreamResolver, subpath);
                            if (temp == null) {
                                throw new RuntimeException("Not found props named:" + temp);
                            }
                            resolveModules(temp.popBaseProperty(PROPS_MODULES));
                            this.props.merge(temp);
                            logPropsFiles(this.mainInputStreamResolver.getViewPath(subpath));
                        }
                    }
                }
            }
        }
    }

    public static interface InputStreamResolver {

        InputStream openInputStream(String path);

        String getViewPath(String path);
    }

    public static class ClasspathInputStreamResolver implements InputStreamResolver {

        private final static String CLASSPATH_PREFIX = "%CLASS_PATH%/";

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
    }
}
