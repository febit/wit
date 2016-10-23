// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.servlet;

import java.io.IOException;
import java.util.Enumeration;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author zqq90
 */
public class WitServlet extends HttpServlet {

    protected WebEngineManager engineManager;
    private String contentType;

    @Override
    @SuppressWarnings("unchecked")
    public void init(final ServletConfig config) throws ServletException {
        super.init(config);
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
        this.engineManager.renderTemplate(request.getServletPath(), ServletUtil.wrapToKeyValues(request, response), response);
    }
}
