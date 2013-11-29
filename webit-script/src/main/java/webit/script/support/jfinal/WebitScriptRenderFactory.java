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
import webit.script.web.ServletUtil;
import webit.script.web.WebEngineManager;

/**
 *
 * @author Zqq
 */
public class WebitScriptRenderFactory implements IMainRenderFactory {

    public final static String DEFAULT_VIEW_EXTENSION = ".wtl";
    protected static final String encoding = Render.getEncoding();
    protected static final String contentType = "text/html; charset=".concat(encoding);

    protected final WebEngineManager engineManager;
    protected final String viewExtension;

    public WebitScriptRenderFactory() {
        this(DEFAULT_VIEW_EXTENSION);
    }

    public WebitScriptRenderFactory(String viewExtension) {
        this.viewExtension = viewExtension;

        this.engineManager
                = new WebEngineManager(JFinal.me().getServletContext())
                .appendProperties("webit.script.Engine.resolvers", "webit.script.support.jfinal.ModelResolver");
    }

    public void setConfigPath(String configPath) {
        this.engineManager.setConfigPath(configPath);
    }

    public void resetEngine() {
        this.engineManager.resetEngine();
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
            final Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("request", request);
            parameters.put("response", response);
            ServletUtil.exportAttributes(parameters, request);
            this.engineManager.renderTemplate(realView, parameters, response);
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
