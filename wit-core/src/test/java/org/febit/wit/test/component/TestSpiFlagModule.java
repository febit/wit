// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.test.component;

import org.febit.wit.Wit;
import org.febit.wit.WitModule;

public class TestSpiFlagModule implements WitModule {

    @Override
    public void apply(Wit wit) {
        var heaps = wit.staticHeaps();
        heaps.constants().set("PLUGIN_SPI_FLAG", true);
    }
}
