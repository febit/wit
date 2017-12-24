// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.plugin;

import java.util.Iterator;
import java.util.ServiceLoader;
import org.febit.wit.Engine;
import org.febit.wit.Init;
import org.febit.wit.loggers.Logger;

/**
 *
 * @since 2.4.0
 * @author zqq90
 */
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
        Iterator<EnginePlugin> iterator = ServiceLoader.load(EnginePlugin.class).iterator();
        while (iterator.hasNext()) {
            EnginePlugin plugin = iterator.next();
            String name = plugin.getClass().getName();
            logger.info("Applying spi plugin: {}.", name);
            engine.inject(name, plugin);
            plugin.apply(engine);
        }
    }
}
