// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.support.springmvc3;

import java.io.IOException;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.servlet.view.AbstractTemplateViewResolver;
import webit.script.web.WebEngineManager;
import webit.script.web.WebEngineManager.ServletContextProvider;

/**
 *
 * @author Zqq
 */
public class WebitScriptViewResolver extends AbstractTemplateViewResolver implements InitializingBean {

    protected final WebEngineManager engineManager;

    @Override
    protected Class<?> requiredViewClass() {
        return WebitScriptView.class;
    }

    public void afterPropertiesSet() throws Exception {
        if (getSuffix() == null || getSuffix().length() == 0) {
            super.setSuffix(this.engineManager.getEngine().getSuffix());
        }
    }

    public WebitScriptViewResolver() {
        setViewClass(requiredViewClass());

        this.engineManager = new WebEngineManager(new ServletContextProvider() {

            public ServletContext getServletContext() {
                return WebitScriptViewResolver.this.getServletContext();
            }
        });
    }

    public void setConfigPath(String configPath) {
        this.engineManager.setConfigPath(configPath);
    }

    public void resetEngine() {
        this.engineManager.resetEngine();
    }

    @Override
    protected WebitScriptView buildView(String viewName) throws Exception {
        WebitScriptView view = (WebitScriptView) super.buildView(viewName);
        view.setResolver(this);
        return view;
    }

    protected void render(String viewName, Map<String, Object> model, final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        this.engineManager.renderTemplate(viewName, model, response);
    }
}
