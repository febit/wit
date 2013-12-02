// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.support.servlet;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import webit.script.web.ServletUtil;
import webit.script.web.WebEngineManager;

/**
 *
 * @author Zqq
 */
public class WebitScriptFilter implements Filter {

    protected WebEngineManager engineManager;
    private String contentType;

    @SuppressWarnings("unchecked")
    public void init(FilterConfig config) throws ServletException {
        engineManager = new WebEngineManager(config.getServletContext());
        String configPath = config.getInitParameter("configPath");
        if (configPath != null && configPath.length() != 0) {
            engineManager.setConfigPath(configPath);
        }
        contentType = config.getInitParameter("contentType");

        //extra settings
        String extraPrefix = config.getInitParameter("extraPrefix");
        if (extraPrefix == null) {
            extraPrefix = "extra.";
        }
        int prefixLength = extraPrefix.length();
        final Enumeration<String> enumeration = config.getInitParameterNames();
        String key;
        while (enumeration.hasMoreElements()) {
            key = enumeration.nextElement();
            if (key.startsWith(extraPrefix)) {
                engineManager.setProperties(key.substring(prefixLength), config.getInitParameter(key));
            }
        }
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        chain.doFilter(request, response);
        if (contentType != null) {
            response.setContentType(contentType);
        }
        final Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("request", request);
        parameters.put("response", response);
        ServletUtil.exportAttributes(parameters, request);
        this.engineManager.renderTemplate(WebitScriptFilterUtil.getTemplatePath(request), parameters, response);
    }

    public void destroy() {
    }
}
