// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.web;

import java.io.IOException;
import java.util.HashMap;
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
    private final ServletContextProvider servletContextProvider;
    private Map<String, Object> extraProperties;
    private Engine engine;

    public WebEngineManager(ServletContextProvider servletContextAware) {
        this.servletContextProvider = servletContextAware;
    }

    public WebEngineManager(ServletContext servletContext) {
        this.servletContextProvider = new DirectServletContextProvider(servletContext);
    }

    private void checkExtraProperties() {
        if (this.extraProperties == null) {
            this.extraProperties = new HashMap<String, Object>();
        }
    }

    public WebEngineManager setProperties(String key, Object value) {
        checkExtraProperties();
        this.extraProperties.put(key, value);
        return this;
    }

    public WebEngineManager setProperties(Map<String, Object> map) {
        checkExtraProperties();
        this.extraProperties.putAll(map);
        return this;
    }

    public WebEngineManager appendProperties(String key, Object value) {
        return setProperties(key.concat("+"), value);
    }

    public Object removeProperties(String key) {
        checkExtraProperties();
        return this.extraProperties.remove(key);
    }

    public void setConfigPath(String configPath) {
        this.configPath = configPath;
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
                    this.servletContextProvider.getServletContext(),
                    this.configPath,
                    this.extraProperties);
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
