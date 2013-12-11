// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.global.impl;

import webit.script.global.GlobalRegister;
import webit.script.global.DefaultGlobalManager;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author zqq90 <zqq_90@163.com>
 */
public class GlobalMapRegister implements GlobalRegister {

    private final static String DEFAULT_NAME = "$GLOBAL";
    private String name = DEFAULT_NAME;

    private final Map vars;

    public GlobalMapRegister() {
        this.vars = new ConcurrentHashMap();
    }

    public void regist(DefaultGlobalManager manager) {
        manager.setConst(this.name, this.vars);
    }

    public Map getVars() {
        return vars;
    }

    public void setName(String name) {
        this.name = name;
    }
}
