// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.support.jfinal;

import com.jfinal.core.JFinal;
import com.jfinal.render.IMainRenderFactory;
import com.jfinal.render.Render;
import com.jfinal.render.RenderException;
import org.febit.wit.servlet.ServletUtil;
import org.febit.wit.servlet.WebEngineManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * @author zqq90
 */
public class WitRenderFactory implements IMainRenderFactory {

    public static final String RESOLVERS = "resolverManager.resolvers";
    protected static final String CONTENT_TYPE = "text/html; charset=" + Render.getEncoding();

    protected final WebEngineManager engineManager;
    protected final String suffix;

    public WitRenderFactory() {
        this(".wit");
    }

    public WitRenderFactory(String suffix) {
        this.suffix = suffix;
        this.engineManager
                = new WebEngineManager(JFinal.me().getServletContext())
                .appendProperties(RESOLVERS, "org.febit.wit.support.jfinal.ModelResolver, org.febit.wit.support.jfinal.RecordResolver");
    }

    public void setConfigPath(String configPath) {
        this.engineManager.setConfigPath(configPath);
    }

    public void resetEngine() {
        this.engineManager.resetEngine();
    }

    public WitRenderFactory setProperties(String key, Object value) {
        this.engineManager.setProperties(key, value);
        return this;
    }

    public WitRenderFactory setProperties(Map<String, Object> map) {
        this.engineManager.setProperties(map);
        return this;
    }

    public WitRenderFactory appendProperties(String key, String value) {
        this.engineManager.appendProperties(key, value);
        return this;
    }

    public Object removeProperties(String key) {
        return this.engineManager.removeProperties(key);
    }

    protected static class WitRender extends Render {

        private final WitRenderFactory renderFactory;

        public WitRender(WitRenderFactory renderFactory, String view) {
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
            response.setContentType(CONTENT_TYPE);
            this.engineManager.renderTemplate(realView, ServletUtil.wrapToKeyValues(request, response), response);
        } catch (IOException ex) {
            throw new RenderException(ex);
        }
    }

    @Override
    public Render getRender(final String view) {
        return new WitRender(this, view);
    }

    @Override
    public String getViewExtension() {
        return suffix;
    }
}
