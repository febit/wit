// Copyright (c) 2013-present, febit.org. All Rights Reserved.
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

    private final Map<Object, Object> vars = new ConcurrentHashMap<>();

    protected String name = "$GLOBAL";

    @Override
    public void regist(GlobalManager manager) {
        manager.setConst(this.name, this.vars);
    }

    public Map<Object, Object> getVars() {
        return vars;
    }
}
