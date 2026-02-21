// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.extern.lib.context;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.febit.wit.Wit;
import org.febit.wit.WitModule;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Accessors(fluent = true)
@RequiredArgsConstructor(staticName = "create")
public class GlobalContextRegister implements WitModule {

    public static final String DEFAULT_NAME = "$GLOBAL";

    @Getter
    private final Map<Object, Object> table = new ConcurrentHashMap<>();

    @Getter
    private final String name;

    public static GlobalContextRegister create() {
        return create(DEFAULT_NAME);
    }

    @Override
    public void apply(Wit wit) {
        var heap = wit.staticHeaps().constant();
        heap.set(this.name, this.table);
    }
}
