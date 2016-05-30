// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.servlet.global;

import javax.servlet.ServletContext;
import webit.script.global.GlobalManager;
import webit.script.global.GlobalRegister;

/**
 *
 * @author zqq90
 * @since 1.4.0
 */
public class GlobalServletRegister implements GlobalRegister {

    protected ServletContext servletContext;
    protected String basePathName = "BASE_PATH";
    protected String servletContextName = "SERVLET_CONTEXT";

    @Override
    public void regist(GlobalManager manager) {
        manager.setConst(this.servletContextName, this.servletContext);
        manager.setConst(this.basePathName, this.servletContext.getContextPath());
    }
}
