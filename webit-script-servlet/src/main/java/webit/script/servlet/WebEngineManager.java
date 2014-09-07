// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import webit.script.Engine;
import webit.script.lang.KeyValues;

/**
 *
 * @author Zqq
 */
public class WebEngineManager {

    private String configPath = "/WEB-INF/webpage.wim";

    private final ServletContextProvider servletContextProvider;
    private Map<String, Object> extraProperties;
    private Engine engine;

    public WebEngineManager(ServletContextProvider servletContextAware) {
        this.servletContextProvider = servletContextAware;
    }

    public WebEngineManager(ServletContext servletContext) {
        this(new DirectServletContextProvider(servletContext));
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

    public WebEngineManager appendProperties(String key, String value) {
        checkExtraProperties();
        key = key.concat("+");
        Object oldValue = this.extraProperties.get(key);
        if (oldValue != null) {
            value = String.valueOf(oldValue) + ',' + value;
        }
        this.extraProperties.put(key, value);
        return this;
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
        }
        return this.engine = ServletEngineUtil.createEngine(
                this.servletContextProvider.getServletContext(),
                this.configPath,
                this.extraProperties);
    }

    public void renderTemplate(final String name, final Map<String, Object> parameters, final HttpServletResponse response) throws IOException {
        getEngine().getTemplate(name)
                .merge(parameters, response.getOutputStream());
    }

    public void renderTemplate(final String name, final KeyValues parameters, final HttpServletResponse response) throws IOException {
        getEngine().getTemplate(name)
                .merge(parameters, response.getOutputStream());
    }

    public void renderTemplate(final String parent, final String name, final KeyValues parameters, final HttpServletResponse response) throws IOException {
        getEngine().getTemplate(parent, name)
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
