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

/**
 *
 * @author Zqq
 */
public class WebitScriptResult extends ActionResult {

    public static final String NAME = "wtl";
    //
    @In(scope = ScopeType.CONTEXT)
    protected MadvocController madvocController;
    //
    protected final WebEngineManager engineManager;

    public WebitScriptResult() {
        super(NAME);
        this.engineManager = new WebEngineManager(new WebEngineManager.ServletContextAware() {

            public ServletContext getServletContext() {
                return WebitScriptResult.this.madvocController.getApplicationContext();
            }
        });

        Map<String, Object> settings = new HashMap<String, Object>(4);
        settings.put("webit.script.Engine.appendLostFileNameExtension", Boolean.TRUE);
        this.engineManager.setExtraSettings(settings);
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
        final Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("request", request);
        parameters.put("response", response);
        ServletUtil.exportAttributes(parameters, request);
        this.engineManager.renderTemplate(resultPath, parameters, response);
    }
}
