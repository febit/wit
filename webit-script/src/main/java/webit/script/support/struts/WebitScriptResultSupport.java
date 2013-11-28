// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.support.struts;

import com.opensymphony.xwork2.ActionInvocation;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.StrutsResultSupport;
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

    public void setConfigPath(String configPath) {
        engineManager.setConfigPath(configPath);
    }

    public void resetEngine() {
        engineManager.resetEngine();
    }

    @Override
    protected void doExecute(String view, ActionInvocation ai) throws Exception {
        Map<String, Object> model = ai.getStack().getContext();
        HttpServletRequest request = (HttpServletRequest) model.get(ServletActionContext.HTTP_REQUEST);
        HttpServletResponse response = (HttpServletResponse) model.get(ServletActionContext.HTTP_RESPONSE);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("request", request);
        params.put("response", response);
        params.put("stackContext", model);
        params.put("action", ai.getAction());
        engineManager.renderTemplate(view, params, response);
    }
}
