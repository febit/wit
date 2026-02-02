// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.global.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.febit.wit.global.GlobalHeap;
import org.febit.wit.global.GlobalHeapRegister;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Accessors(fluent = true)
@RequiredArgsConstructor(staticName = "create")
public class GlobalContextRegister implements GlobalHeapRegister {

    public static final String DEFAULT_NAME = "$GLOBAL";

    @Getter
    private final Map<Object, Object> vars = new ConcurrentHashMap<>();

    @Getter
    private final String name;

    public static GlobalContextRegister create() {
        return create(DEFAULT_NAME);
    }

    @Override
    public void register(GlobalHeap heap) {
        heap.setConst(this.name, this.vars);
    }
}
