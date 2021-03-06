// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.plugin;

import lombok.val;
import org.febit.wit.Engine;
import org.febit.wit.Init;
import org.febit.wit.loggers.Logger;

import java.util.ServiceLoader;

/**
 * @author zqq90
 * @since 2.4.0
 */
@SuppressWarnings({
        "WeakerAccess"
})
public class SpiPluginCollector {

    protected Engine engine;
    protected Logger logger;

    // settings:
    protected boolean enable;

    @Init
    public void init() {
        if (!enable) {
            logger.info("SpiPluginCollector is disabled.");
            return;
        }
        for (val plugin : ServiceLoader.load(EnginePlugin.class)) {
            String name = plugin.getClass().getName();
            logger.info("Applying spi plugin: {}.", name);
            engine.inject(name, plugin);
            plugin.apply(engine);
        }
    }
}
