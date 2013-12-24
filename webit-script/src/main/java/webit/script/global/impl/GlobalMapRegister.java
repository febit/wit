// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.global.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import webit.script.global.GlobalManager;
import webit.script.global.GlobalRegister;

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

    public void regist(GlobalManager manager) {
        manager.getConstBag().set(this.name, this.vars);
    }

    public Map getVars() {
        return vars;
    }

    public void setName(String name) {
        this.name = name;
    }
}
