// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.plugin;

import org.febit.wit.Engine;

/**
 * @author zqq90
 * @since 2.4.0
 */
@FunctionalInterface
public interface EnginePlugin {

    void apply(Engine engine);
}
