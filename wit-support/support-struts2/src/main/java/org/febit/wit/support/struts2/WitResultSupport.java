// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.support.struts2;

import com.opensymphony.xwork2.ActionInvocation;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.StrutsResultSupport;
import org.febit.wit.Vars;
import org.febit.wit.servlet.WebEngineManager;

/**
 *
 * @author zqq90
 */
public class WitResultSupport extends StrutsResultSupport {

    private final static WebEngineManager engineManager;

    static {
        engineManager = new WebEngineManager(ServletActionContext::getServletContext);
    }

    private static String contentType;

    public static void setConfigPath(String configPath) {
        engineManager.setConfigPath(configPath);
    }

    public static void setContentType(String contentType) {
        WitResultSupport.contentType = contentType;
    }

    public static void resetEngine() {
        engineManager.resetEngine();
    }

    private final static String[] PARAM_KEYS = new String[]{
        "request", "response", "stackContext", "action"
    };

    @Override
    protected void doExecute(String view, ActionInvocation ai) throws Exception {
        final Map<String, Object> context = ai.getStack().getContext();
        final HttpServletRequest request = (HttpServletRequest) context.get(ServletActionContext.HTTP_REQUEST);
        final HttpServletResponse response = (HttpServletResponse) context.get(ServletActionContext.HTTP_RESPONSE);
        if (contentType != null) {
            response.setContentType(contentType);
        }
        engineManager.renderTemplate(view, Vars.of(PARAM_KEYS, new Object[]{
            request,
            response,
            context,
            ai.getAction()
        }), response);
    }
}
