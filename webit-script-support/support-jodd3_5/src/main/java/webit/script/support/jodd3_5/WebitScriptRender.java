// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.support.jodd3_5;

import java.util.HashMap;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import jodd.madvoc.ActionRequest;
import jodd.madvoc.MadvocUtil;
import jodd.madvoc.ResultPath;
import jodd.madvoc.ScopeType;
import jodd.madvoc.component.MadvocController;
import jodd.madvoc.component.ResultMapper;
import jodd.madvoc.meta.In;
import jodd.madvoc.result.BaseActionResult;
import webit.script.CFG;
import webit.script.web.ServletUtil;
import webit.script.web.WebEngineManager;

/**
 *
 * @author zqq90
 */
public class WebitScriptRender extends BaseActionResult<WebitScriptRenderData> implements WebEngineManager.ServletContextProvider {

    public static final WebitScriptRenderData DEFAULT_RESULT = new WebitScriptRenderData(null, null);

    public static WebitScriptRenderData render() {
        return DEFAULT_RESULT;
    }

    public static WebitScriptRenderData render(String path) {
        return new WebitScriptRenderData(path, null);
    }

    public static WebitScriptRenderData render(String path, String contentType) {
        return new WebitScriptRenderData(path, contentType);
    }

    @In(scope = ScopeType.CONTEXT)
    protected ResultMapper resultMapper;
    @In(scope = ScopeType.CONTEXT)
    protected MadvocController madvocController;
    protected String contentType;
    //
    protected final WebEngineManager engineManager;

    protected HashMap<String, String> targetCache;

    @Override
    public void render(final ActionRequest actionRequest, final WebitScriptRenderData resultValue) throws Exception {
        final HttpServletResponse response = actionRequest.getHttpServletResponse();

        if (resultValue.contentType != null) {
            response.setContentType(resultValue.contentType);
        } else if (contentType != null) {
            response.setContentType(contentType);
        }

        String key = actionRequest.getActionPath();
        final String customPath;
        if ((customPath = resultValue.path) != null) {
            key += ':' + customPath;
        }
        String target = targetCache.get(key);

        if (target == null) {

            ResultPath resultPath = resultMapper.resolveResultPath(actionRequest.getActionPath(), customPath);

            String actionPath = resultPath.getPath();
            String path = actionPath;
            String value = resultPath.getValue();

            while (true) {
                // variant #1: with value
                if (value != null) {
                    if (path == null) {
                        // only value remains
                        int lastSlashNdx = actionPath.lastIndexOf('/');
                        if (lastSlashNdx != -1) {
                            target = actionPath.substring(0, lastSlashNdx + 1) + value;
                        } else {
                            target = '/' + value;
                        }
                    } else {
                        target = path + '.' + value;
                    }

                    if (targetExist(target)) {
                        break;
                    }
                }

                // variant #1: without value
                if (path != null) {
                    target = path;

                    if (targetExist(target)) {
                        break;
                    }
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Result not found: " + resultPath);
                    return;
                }

                int dotNdx = MadvocUtil.lastIndexOfDotAfterSlash(path);
                if (dotNdx == -1) {
                    path = null;
                } else {
                    path = path.substring(0, dotNdx);
                }
            }

            // store target in cache
            targetCache.put(key, target);
        }

        this.engineManager.renderTemplate(
                target,
                ServletUtil.wrapToKeyValues(actionRequest.getHttpServletRequest(), response), response);
    }

    protected boolean targetExist(String path) {
        return this.engineManager.getEngine().exists(path);
    }

    public WebitScriptRender() {
        this.targetCache = new HashMap<String, String>();
        this.engineManager
                = new WebEngineManager(this)
                .setProperties(CFG.APPEND_LOST_SUFFIX, Boolean.TRUE);
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

}
