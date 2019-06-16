// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.support.springmvc3;

import org.springframework.web.servlet.view.AbstractTemplateView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author zqq90
 */
public class WitView extends AbstractTemplateView {

    private WitViewResolver resolver;

    public void setResolver(WitViewResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    protected void renderMergedTemplateModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        model.put("request", request);
        model.put("response", response);
        this.resolver.render(getUrl(), model, request, response);
    }
}
