// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.servlet;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletContext;
import org.febit.wit.Engine;
import org.febit.wit.util.Props;
import org.febit.wit.util.PropsUtil;

/**
 *
 * @author zqq90
 */
public class ServletEngineUtil {

    private static final String SERVLET_CONTEXT = "servlet.servletContext";
    private static final String DEFAULT_WEB_PROPERTIES = "/default-servlet.wim";

    public static Engine createEngine(final ServletContext servletContext, final String configFiles) {
        return createEngine(servletContext, configFiles, null);
    }

    @SuppressWarnings("unchecked")
    public static Engine createEngine(final ServletContext servletContext, final String configFiles, final Map<String, Object> extraSettings) {
        final Map<String, Object> settings;
        final Props props;
        props = loadProps(Engine.createConfigProps(DEFAULT_WEB_PROPERTIES), servletContext, configFiles);
        settings = new HashMap<>();
        settings.put(SERVLET_CONTEXT, servletContext);
        if (extraSettings != null) {
            settings.putAll(extraSettings);
        }
        return Engine.create(props, settings);
    }

    public static Props loadProps(final Props props, final ServletContext servletContext, final String... paths) {
        return PropsUtil.load(props, new ServletContextInputResolver(servletContext), paths);
    }

    private static class ServletContextInputResolver implements PropsUtil.InputResolver {

        private final ServletContext servletContext;

        ServletContextInputResolver(ServletContext servletContext) {
            this.servletContext = servletContext;
        }

        @Override
        public InputStream openInputStream(String path) {
            final InputStream in;
            if ((in = servletContext.getResourceAsStream(path)) != null) {
                return in;
            }
            try {
                //try read file by real path
                return new FileInputStream(servletContext.getRealPath(path));
            } catch (FileNotFoundException ignore) {
            }
            return null;
        }

        @Override
        public String getViewPath(String path) {
            return "servlet-path:".concat(fixModuleName(path));
        }

        @Override
        public String fixModuleName(String path) {
            return path.charAt(0) == '/'
                    ? path.substring(1)
                    : path;
        }
    }

}
