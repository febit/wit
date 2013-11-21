// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.support.jodd.madvoc;

import java.util.HashMap;
import java.util.Map;
import jodd.madvoc.ActionRequest;
import jodd.madvoc.ScopeType;
import jodd.madvoc.component.MadvocController;
import jodd.madvoc.meta.In;
import jodd.madvoc.result.ActionResult;
import webit.script.Engine;
import webit.script.web.ServletEngineUtil;
import webit.script.web.HttpServletTemplateRender;

/**
 *
 * @author Zqq
 */
public class WebitScriptResult extends ActionResult {

    public static final String NAME = "wtl";
    protected String configPath = "/WEB-INF/webit-script-web-page.props";
    //
    @In(scope = ScopeType.CONTEXT)
    protected MadvocController madvocController;
    //
    protected Engine _engine;
    protected final HttpServletTemplateRender render;

    public WebitScriptResult() {
        super(NAME);
        render = new HttpServletTemplateRender();
    }

    public void setBufferSize(int bufferSize) {
        this.render.setBufferSize(bufferSize);
    }

    public void setConfigPath(String configPath) {
        this.configPath = configPath;
    }

    public void resetEngine() {
        this._engine = null;
    }

    protected Engine getEngine() {
        Engine engine;
        if ((engine = this._engine) != null) {
            return engine;
        } else {
            Map<String, Object> settings = new HashMap<String, Object>();
            settings.put("webit.script.Engine.appendLostFileNameExtension", Boolean.TRUE);
            return this._engine = ServletEngineUtil.createEngine(
                    this.madvocController.getApplicationContext(),
                    this.configPath,
                    settings);
        }
    }

    @Override
    public void render(ActionRequest actionRequest, Object resultObject, String resultValue, String resultPath) throws Exception {
        this.render.render(
                getEngine().getTemplate(resultPath),
                actionRequest.getHttpServletRequest(),
                actionRequest.getHttpServletResponse());
    }
}
