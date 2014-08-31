// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.global.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import webit.script.global.GlobalManager;
import webit.script.global.GlobalRegister;

/**
 *
 * @author zqq90
 */
public class GlobalMapRegister implements GlobalRegister {

    private String name;

    private final Map vars;

    public GlobalMapRegister() {
        this.name = "$GLOBAL";
        this.vars = new ConcurrentHashMap();
    }

    public void regist(GlobalManager manager) {
        manager.setConst(this.name, this.vars);
    }

    public Map getVars() {
        return vars;
    }

    public void setName(String name) {
        this.name = name;
    }
}
