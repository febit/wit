// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.heap;

import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
public class StaticHeaps {

    @Getter
    private final Heap constants = GenricHeap.concurrent();
    @Getter
    private final Heap variables = GenricHeap.concurrent();

    public void clear() {
        this.constants().clear();
        this.variables().clear();
    }
}
