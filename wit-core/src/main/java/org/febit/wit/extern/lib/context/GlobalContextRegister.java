// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.extern.lib.context;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.febit.wit.Engine;
import org.febit.wit.EngineModule;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Accessors(fluent = true)
@RequiredArgsConstructor(staticName = "create")
public class GlobalContextRegister implements EngineModule {

    public static final String DEFAULT_NAME = "$GLOBAL";

    @Getter
    private final Map<Object, Object> table = new ConcurrentHashMap<>();

    @Getter
    private final String name;

    public static GlobalContextRegister create() {
        return create(DEFAULT_NAME);
    }

    @Override
    public void apply(Engine engine) {
        var heap = engine.staticHeaps().constant();
        heap.set(this.name, this.table);
    }
}
