// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.test.component;

import org.febit.wit.Wit;
import org.febit.wit.WitModule;

public class TestConfigFlagModule implements WitModule {

    @Override
    public void apply(Wit wit) {
        wit.staticHeaps().constant().set("PLUGIN_CONFIG_FLAG", true);
    }
}
