// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.support.struts;

import com.opensymphony.xwork2.ActionInvocation;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.StrutsResultSupport;
import webit.script.util.keyvalues.KeyValuesUtil;
import webit.script.web.WebEngineManager;
import webit.script.web.WebEngineManager.ServletContextProvider;

/**
 *
 * @author Zqq
 */
public class WebitScriptResultSupport extends StrutsResultSupport {

    private final static WebEngineManager engineManager;

    static {
        engineManager = new WebEngineManager(new ServletContextProvider() {

            public ServletContext getServletContext() {
                return ServletActionContext.getServletContext();
            }
        });
    }

    private static String contentType;

    public static void setConfigPath(String configPath) {
        engineManager.setConfigPath(configPath);
    }

    public static void setContentType(String contentType) {
        WebitScriptResultSupport.contentType = contentType;
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
        engineManager.renderTemplate(view, KeyValuesUtil.wrap(PARAM_KEYS, new Object[]{
            request,
            response,
            context,
            ai.getAction()
        }), response);
    }
}
