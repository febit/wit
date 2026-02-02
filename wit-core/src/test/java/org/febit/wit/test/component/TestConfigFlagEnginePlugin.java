// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.test.component;

import org.febit.wit.Engine;
import org.febit.wit.EnginePlugin;

public class TestConfigFlagEnginePlugin implements EnginePlugin {

    @Override
    public void apply(Engine engine) {
        engine.globalHeap().setConst("PLUGIN_CONFIG_FLAG", true);
    }
}
