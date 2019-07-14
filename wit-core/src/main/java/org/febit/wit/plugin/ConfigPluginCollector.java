// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.plugin;

import org.febit.wit.Engine;
import org.febit.wit.Init;

/**
 * @author zqq90
 * @since 2.4.0
 */
@SuppressWarnings({
        "WeakerAccess"
})
public class ConfigPluginCollector {

    protected Engine engine;
    protected EnginePlugin[] plugins;

    @Init
    public void init() {
        if (plugins != null) {
            for (EnginePlugin plugin : plugins) {
                plugin.apply(engine);
            }
        }
    }
}
