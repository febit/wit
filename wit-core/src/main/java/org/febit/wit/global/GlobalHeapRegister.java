// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.global;

import org.febit.wit.Engine;
import org.febit.wit.EnginePlugin;

@FunctionalInterface
public interface GlobalHeapRegister extends EnginePlugin {

    void register(GlobalHeap heap);

    default void apply(Engine engine) {
        this.register(engine.globalHeap());
    }
}
