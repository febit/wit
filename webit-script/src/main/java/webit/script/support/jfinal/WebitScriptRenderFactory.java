// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.support.jfinal;

import com.jfinal.core.JFinal;
import com.jfinal.render.IMainRenderFactory;
import com.jfinal.render.Render;
import com.jfinal.render.RenderException;
import java.io.IOException;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import webit.script.CFG;
import webit.script.Engine;
import webit.script.web.ServletUtil;
import webit.script.web.WebEngineManager;

/**
 *
 * @author Zqq
 */
public class WebitScriptRenderFactory implements IMainRenderFactory {

    protected static final String encoding = Render.getEncoding();
    protected static final String contentType = "text/html; charset=".concat(encoding);

    protected final WebEngineManager engineManager;
    protected final String suffix;

    public WebitScriptRenderFactory() {
        this(Engine.DEFAULT_SUFFIX);
    }

    public WebitScriptRenderFactory(String suffix) {
        this.suffix = suffix;
        this.engineManager
                = new WebEngineManager(JFinal.me().getServletContext())
                .appendProperties(CFG.RESOLVERS, "webit.script.support.jfinal.ModelResolver, webit.script.support.jfinal.RecordResolver");
    }

    public void setConfigPath(String configPath) {
        this.engineManager.setConfigPath(configPath);
    }

    public void resetEngine() {
        this.engineManager.resetEngine();
    }

    public WebitScriptRenderFactory setProperties(String key, Object value) {
        this.engineManager.setProperties(key, value);
        return this;
    }

    public WebitScriptRenderFactory setProperties(Map<String, Object> map) {
        this.engineManager.setProperties(map);
        return this;
    }

    public WebitScriptRenderFactory appendProperties(String key, Object value) {
        this.engineManager.appendProperties(key, value);
        return this;
    }

    public Object removeProperties(String key) {
        return this.engineManager.removeProperties(key);
    }

    private static class WebitScriptRender extends Render {

        private final WebitScriptRenderFactory renderFactory;

        public WebitScriptRender(WebitScriptRenderFactory renderFactory, String view) {
            this.view = view;
            this.renderFactory = renderFactory;
        }

        @Override
        public void render() {
            renderFactory.render(view, request, response);
        }
    }

    protected void render(final String realView, final HttpServletRequest request, final HttpServletResponse response) {
        try {
            response.setContentType(contentType);
            this.engineManager.renderTemplate(realView, ServletUtil.wrapToKeyValues(request, response), response);
        } catch (IOException ex) {
            throw new RenderException(ex);
        }
    }

    public Render getRender(final String view) {
        return new WebitScriptRender(this, view);
    }

    public String getViewExtension() {
        return suffix;
    }
}
