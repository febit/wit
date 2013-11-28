// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.support.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import webit.script.web.ServletUtil;
import webit.script.web.WebEngineManager;

/**
 *
 * @author Zqq
 */
public class WebitScriptServlet extends HttpServlet {

    protected WebEngineManager engineManager;
    private String contentType;

    @Override
    public void init(final ServletConfig config) throws ServletException {
        super.init(config);
        engineManager = new WebEngineManager(config.getServletContext());
        String configPath = config.getInitParameter("configPath");
        if (configPath != null && configPath.length() != 0) {
            engineManager.setConfigPath(configPath);
        }
        contentType = config.getInitParameter("contentType");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (contentType != null) {
            response.setContentType(contentType);
        }
        final Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("request", request);
        parameters.put("response", response);
        ServletUtil.exportAttributes(parameters, request);
        this.engineManager.renderTemplate(request.getServletPath(), parameters, response);
    }
}
