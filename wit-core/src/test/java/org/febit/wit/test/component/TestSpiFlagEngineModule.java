// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.test.component;

import org.febit.wit.Engine;
import org.febit.wit.EngineModule;

public class TestSpiFlagEngineModule implements EngineModule {

    @Override
    public void apply(Engine engine) {
        var heaps = engine.staticHeaps();
        heaps.constant().set("PLUGIN_SPI_FLAG", true);
    }
}
