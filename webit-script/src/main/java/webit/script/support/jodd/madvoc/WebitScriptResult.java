// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.support.jodd.madvoc;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import jodd.madvoc.ActionRequest;
import jodd.madvoc.ScopeType;
import jodd.madvoc.component.MadvocController;
import jodd.madvoc.meta.In;
import jodd.madvoc.result.ActionResult;
import webit.script.web.ServletUtil;
import webit.script.web.WebEngineManager;
import webit.script.web.WebEngineManager.ServletContextProvider;

/**
 *
 * @author Zqq
 */
public class WebitScriptResult extends ActionResult implements ServletContextProvider {

    public static final String NAME = "wtl";
    //
    @In(scope = ScopeType.CONTEXT)
    protected MadvocController madvocController;
    protected String contentType;
    //
    protected final WebEngineManager engineManager;

    public WebitScriptResult() {
        super(NAME);
        this.engineManager
                = new WebEngineManager(this)
                .setProperties("webit.script.Engine.appendLostFileNameExtension", Boolean.TRUE);
    }

    public ServletContext getServletContext() {
        return this.madvocController.getApplicationContext();
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setConfigPath(String configPath) {
        this.engineManager.setConfigPath(configPath);
    }

    public void resetEngine() {
        this.engineManager.resetEngine();
    }

    @Override
    public void render(ActionRequest actionRequest, Object resultObject, String resultValue, String resultPath) throws Exception {
        final HttpServletRequest request = actionRequest.getHttpServletRequest();
        final HttpServletResponse response = actionRequest.getHttpServletResponse();
        if (contentType != null) {
            response.setContentType(contentType);
        }
        final Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("request", request);
        parameters.put("response", response);
        ServletUtil.exportAttributes(parameters, request);
        this.engineManager.renderTemplate(resultPath, parameters, response);
    }
}
