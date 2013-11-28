// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.web;

import java.io.IOException;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import webit.script.Engine;

/**
 *
 * @author Zqq
 */
public class WebEngineManager {

    private final static String DEFAULT_CONFIG = "/WEB-INF/webit-script-webpage.props";
    private String configPath = DEFAULT_CONFIG;
    private final ServletContextProvider servletContextAware;
    private Map<String, Object> extraSettings;
    private Engine engine;

    public WebEngineManager(ServletContextProvider servletContextAware) {
        this.servletContextAware = servletContextAware;
    }

    public WebEngineManager(ServletContext servletContext) {
        this.servletContextAware = new DirectServletContextProvider(servletContext);
    }

    public void setConfigPath(String configPath) {
        this.configPath = configPath;
    }

    public void setExtraSettings(Map<String, Object> extraSettings) {
        this.extraSettings = extraSettings;
    }

    public void resetEngine() {
        this.engine = null;
    }

    public Engine getEngine() {
        Engine engine;
        if ((engine = this.engine) != null) {
            return engine;
        } else {
            return this.engine = ServletEngineUtil.createEngine(
                    this.servletContextAware.getServletContext(),
                    this.configPath,
                    this.extraSettings);
        }
    }

    public void renderTemplate(final String name, final Map<String, Object> parameters, final HttpServletResponse response) throws IOException {
        getEngine()
                .getTemplate(name)
                .merge(parameters, response.getOutputStream());
    }

    public static interface ServletContextProvider {

        ServletContext getServletContext();
    }

    public static class DirectServletContextProvider implements ServletContextProvider {

        private final ServletContext servletContext;

        public DirectServletContextProvider(ServletContext servletContext) {
            this.servletContext = servletContext;
        }

        public ServletContext getServletContext() {
            return this.servletContext;
        }
    }
}
