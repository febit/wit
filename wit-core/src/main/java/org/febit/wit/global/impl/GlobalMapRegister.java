// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.global.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.febit.wit.global.GlobalManager;
import org.febit.wit.global.GlobalRegister;

/**
 *
 * @author zqq90
 */
public class GlobalMapRegister implements GlobalRegister {

    protected String name;

    private final Map vars;

    public GlobalMapRegister() {
        this.name = "$GLOBAL";
        this.vars = new ConcurrentHashMap();
    }

    @Override
    public void regist(GlobalManager manager) {
        manager.setConst(this.name, this.vars);
    }

    public Map getVars() {
        return vars;
    }
}
