// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.test.component;

import org.febit.wit.Engine;
import org.febit.wit.EnginePlugin;

public class TestSpiFlagEnginePlugin implements EnginePlugin {

    @Override
    public void apply(Engine engine) {
        var globalManager = engine.globalHeap();
        globalManager.setConst("PLUGIN_SPI_FLAG", true);
    }
}
