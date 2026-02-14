// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.test.component;

import org.febit.wit.Engine;
import org.febit.wit.EngineModule;

public class TestConfigFlagEngineModule implements EngineModule {

    @Override
    public void apply(Engine engine) {
        engine.staticHeaps().constant().set("PLUGIN_CONFIG_FLAG", true);
    }
}
