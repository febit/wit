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
import webit.script.servlet.ServletUtil;
import webit.script.servlet.WebEngineManager;

/**
 *
 * @author Zqq
 */
public class WebitRenderFactory implements IMainRenderFactory {

    protected static final String encoding = Render.getEncoding();
    protected static final String contentType = "text/html; charset=" + encoding;

    protected final WebEngineManager engineManager;
    protected final String suffix;

    public WebitRenderFactory() {
        this(Engine.DEFAULT_SUFFIX);
    }

    public WebitRenderFactory(String suffix) {
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

    public WebitRenderFactory setProperties(String key, Object value) {
        this.engineManager.setProperties(key, value);
        return this;
    }

    public WebitRenderFactory setProperties(Map<String, Object> map) {
        this.engineManager.setProperties(map);
        return this;
    }

    public WebitRenderFactory appendProperties(String key, String value) {
        this.engineManager.appendProperties(key, value);
        return this;
    }

    public Object removeProperties(String key) {
        return this.engineManager.removeProperties(key);
    }

    protected static class WebitRender extends Render {

        private final WebitRenderFactory renderFactory;

        public WebitRender(WebitRenderFactory renderFactory, String view) {
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

    @Override
    public Render getRender(final String view) {
        return new WebitRender(this, view);
    }

    @Override
    public String getViewExtension() {
        return suffix;
    }
}
