// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.util;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.febit.wit.exceptions.IllegalConfigException;

/**
 *
 * @author zqq90
 */
public class PropsUtil {

    public static final ClasspathInputResolver CLASSPATH_INPUT_RESOLVER = new ClasspathInputResolver();

    private PropsUtil() {
    }

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
        private final CharArrayWriter _charsBuffer;

        private Set<String> loadedModules;
        private Map<String, Props> modulePropsCache;

        PropsLoader(Props props) {
            this.props = props;
            this._buffer = new char[3072];
            this._charsBuffer = new CharArrayWriter();
        }

        private void mergeProps(Props src, String name) {
            if (this.props.containsModule(name)) {
                return;
            }
            this.props.merge(src);
            this.props.addModule(name);
        }

        private void resolveModules(Props src) {
            resolveModules(src.remove("@modules"));
        }

        private void resolveModules(String modules) {
            if (modules == null) {
                return;
            }
            if (this.loadedModules == null) {
                this.loadedModules = new HashSet<>();
            }
            if (this.modulePropsCache == null) {
                this.modulePropsCache = new HashMap<>();
            }
            for (String module : StringUtil.toArray(modules)) {
                if (loadedModules.contains(module)) {
                    continue;
                }
                Props moduleProps = modulePropsCache.get(module);
                if (moduleProps == null) {
                    moduleProps = loadProps(CLASSPATH_INPUT_RESOLVER, module);
                    modulePropsCache.put(module, moduleProps);
                    resolveModules(moduleProps);
                    if (loadedModules.contains(module)) {
                        // self depended!
                        continue;
                    }
                }
                loadedModules.add(module);
                mergeProps(moduleProps, CLASSPATH_INPUT_RESOLVER.getViewPath(module));
            }
        }

        private Props loadProps(InputResolver inputResolver, final String path) {
            final CharArrayWriter charsBuffer = this._charsBuffer;
            final char[] buffer = this._buffer;
            final InputStream in = inputResolver.openInputStream(path);
            if (in == null) {
                throw new IllegalConfigException("Not found props: ".concat(inputResolver.getViewPath(path)));
            }
            try (Reader reader = new InputStreamReader(in, "UTF-8")) {
                charsBuffer.reset();
                int read;
                while ((read = reader.read(buffer)) >= 0) {
                    charsBuffer.write(buffer, 0, read);
                }
                final Props tempProps = new Props();
                tempProps.load(charsBuffer.toCharArray());
                charsBuffer.reset();
                return tempProps;
            } catch (IOException ex) {
                throw new IllegalConfigException("Not found props: ".concat(inputResolver.getViewPath(path)), ex);
            }
        }

        void load(InputResolver inputResolver, final String... paths) {
            if (inputResolver instanceof ClasspathInputResolver) {
                for (String modules : paths) {
                    resolveModules(modules);
                }
            } else {
                for (String path : paths) {
                    for (String subpath : StringUtil.toArray(path)) {
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

        String fixModuleName(String path);
    }

    public static class ClasspathInputResolver implements InputResolver {

        ClasspathInputResolver() {
        }

        @Override
        public InputStream openInputStream(String path) {
            return ClassUtil.getDefaultClassLoader().getResourceAsStream(fixModuleName(path));
        }

        @Override
        public String getViewPath(String path) {
            return "classpath:".concat(fixModuleName(path));
        }

        @Override
        public String fixModuleName(String path) {
            return path.charAt(0) == '/'
                    ? path.substring(1)
                    : path;
        }
    }
}
