// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;

import java.util.function.ObjIntConsumer;

@RequiredArgsConstructor
public final class FrameIndexer {

    public static final FrameIndexer EMPTY = new FrameIndexer(
            null, new String[0], new int[0]);

    @Nullable
    private final FrameIndexer upstream;

    private final String[] names;
    private final int[] indexes;

    public int lookup(final String name) {
        var myNames = this.names;
        for (int i = 0, len = myNames.length; i < len; i++) {
            if (myNames[i].equals(name)) {
                return indexes[i];
            }
        }
        return -1;
    }

    public int lookupUpstream(final String name) {
        var index = lookup(name);
        if (index != -1) {
            return index;
        }
        if (this.upstream != null) {
            return upstream.lookupUpstream(name);
        }
        return -1;
    }

    public void each(ObjIntConsumer<String> action) {
        var myNames = this.names;
        var myIndexes = this.indexes;
        for (int i = 0, len = myNames.length; i < len; i++) {
            action.accept(myNames[i], myIndexes[i]);
        }
    }

    public String name(int index) {
        return this.names[index];
    }
}
