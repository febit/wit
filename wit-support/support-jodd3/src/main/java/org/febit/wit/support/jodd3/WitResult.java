// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.support.jodd3;

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
import org.febit.wit.CFG;
import org.febit.wit.servlet.ServletUtil;
import org.febit.wit.servlet.WebEngineManager;

/**
 *
 * @author zqq90
 */
public class WitResult extends BaseActionResult<Object> implements WebEngineManager.ServletContextProvider {

    public static final WitData DEFAULT_RESULT = new WitData(null, null);

    public static final String NAME = "wit";

    public WitResult() {
        super(NAME);
        this.targetCache = new HashMap<>();
        this.engineManager
                = new WebEngineManager(this)
                        .setProperties(CFG.APPEND_LOST_SUFFIX, Boolean.TRUE);
    }

    public static WitData render() {
        return DEFAULT_RESULT;
    }

    public static WitData render(String path) {
        return new WitData(path, null);
    }

    public static WitData render(String path, String contentType) {
        return new WitData(path, contentType);
    }

    @In(scope = ScopeType.CONTEXT)
    protected ResultMapper resultMapper;

    @In(scope = ScopeType.CONTEXT)
    protected MadvocController madvocController;

    protected String contentType;

    protected final WebEngineManager engineManager;
    protected HashMap<String, String> targetCache;

    @Override
    public void render(final ActionRequest actionRequest, final Object resultValue) throws Exception {
        final HttpServletResponse response = actionRequest.getHttpServletResponse();

        boolean contentTypeSetted = false;
        final String customPath;

        if (resultValue == null) {
            customPath = null;
        } else if (resultValue instanceof WitData) {
            final WitData data = (WitData) resultValue;
            if (data.contentType != null) {
                response.setContentType(data.contentType);
                contentTypeSetted = true;
            }
            customPath = data.path;
        } else {
            customPath = resultValue.toString();
        }

        if (!contentTypeSetted && contentType != null) {
            response.setContentType(contentType);
        }

        String key = actionRequest.getActionPath();
        if (customPath != null) {
            key += ':' + customPath;
        }
        String target;
        if ((target = targetCache.get(key)) == null) {

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

    @Override
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
