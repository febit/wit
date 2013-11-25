// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.support.jfinal;

import com.jfinal.core.JFinal;
import com.jfinal.render.IMainRenderFactory;
import com.jfinal.render.Render;
import com.jfinal.render.RenderException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import webit.script.Engine;
import webit.script.web.HttpServletTemplateRender;
import webit.script.web.ServletEngineUtil;

/**
 *
 * @author Zqq
 */
public class WebitScriptRenderFactory implements IMainRenderFactory {

    public final static String DEFAULT_VIEW_EXTENSION = ".wtl";
    protected static final String encoding = Render.getEncoding();
    protected static final String contentType = "text/html; charset=".concat(encoding);

    protected String configPath = "/WEB-INF/webit-script-web-page.props";
    protected Engine _engine;
    protected final String viewExtension;
    protected final HttpServletTemplateRender render;

    public WebitScriptRenderFactory() {
        this(DEFAULT_VIEW_EXTENSION);
    }

    public WebitScriptRenderFactory(String viewExtension) {
        this.viewExtension = viewExtension;
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
            Map<String, Object> settings = new HashMap<String, Object>(4);
            settings.put("webit.script.Engine.resolvers+", "webit.script.support.jfinal.ModelResolver");
            return this._engine = ServletEngineUtil.createEngine(
                    JFinal.me().getServletContext(),
                    this.configPath,
                    settings);
        }
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

    protected void render(String realView, final HttpServletRequest request, final HttpServletResponse response) {
        try {
            response.setContentType(contentType);
            this.render.render(
                    this.getEngine().getTemplate(realView),
                    request,
                    response);
        } catch (IOException ex) {
            throw new RenderException(ex);
        }
    }

    public Render getRender(final String view) {
        return new WebitScriptRender(this, view);
    }

    public String getViewExtension() {
        return viewExtension;
    }
}
