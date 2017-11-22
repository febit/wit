// Copyright (c) 2013-2017, febit.org. All Rights Reserved.
package org.febit.wit.plugin;

import org.febit.wit.Engine;
import org.febit.wit.Init;

/**
 * 
 * @since 2.4.0
 * @author zqq90
 */
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
