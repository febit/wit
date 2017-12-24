// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.test;

import org.febit.wit.Engine;
import org.febit.wit.global.GlobalManager;
import org.febit.wit.plugin.EnginePlugin;

/**
 *
 * @author zqq90
 */
public class TestConfigEnginePlugin implements EnginePlugin {

    protected GlobalManager globalManager;

    @Override
    public void apply(Engine engine) {
        globalManager.setConst("PLUGIN_CONFIG_FLAG", true);
    }
}
