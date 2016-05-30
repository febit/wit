// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.support.springmvc3;

import java.io.IOException;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.view.AbstractTemplateViewResolver;
import webit.script.servlet.WebEngineManager;
import webit.script.servlet.WebEngineManager.ServletContextProvider;

/**
 *
 * @author Zqq
 */
public class WebitViewResolver extends AbstractTemplateViewResolver {

    protected final WebEngineManager engineManager;

    @Override
    protected Class<?> requiredViewClass() {
        return WebitView.class;
    }

    public WebitViewResolver() {
        setViewClass(requiredViewClass());

        this.engineManager = new WebEngineManager(new ServletContextProvider() {

            @Override
            public ServletContext getServletContext() {
                return WebitViewResolver.this.getServletContext();
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
    protected WebitView buildView(String viewName) throws Exception {
        WebitView view = (WebitView) super.buildView(viewName);
        view.setResolver(this);
        return view;
    }

    protected void render(String viewName, Map<String, Object> model, final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        this.engineManager.renderTemplate(viewName, model, response);
    }
}
