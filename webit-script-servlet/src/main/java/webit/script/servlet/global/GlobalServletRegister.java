// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.servlet.global;

import javax.servlet.ServletContext;
import webit.script.CFG;
import webit.script.Engine;
import webit.script.Initable;
import webit.script.global.GlobalManager;
import webit.script.global.GlobalRegister;

/**
 *
 * @author zqq90
 * @since 1.4.0
 */
public class GlobalServletRegister implements GlobalRegister, Initable {

    private ServletContext servletContext;
    private String basePathName = "BASE_PATH";
    private String servletContextName = "SERVLET_CONTEXT";

    @Override
    public void init(Engine engine) {
        if (this.servletContext == null) {
            this.servletContext = (ServletContext) engine.getConfig(CFG.SERVLET_CONTEXT);
        }
    }

    public void regist(GlobalManager manager) {
        manager.setConst(this.servletContextName, this.servletContext);
        manager.setConst(this.basePathName, this.servletContext.getContextPath());
    }

    public void setServletContextName(String servletContextName) {
        this.servletContextName = servletContextName;
    }

    public void setBasePathName(String basePathName) {
        this.basePathName = basePathName;
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}
