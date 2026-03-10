// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;

import java.util.function.ObjIntConsumer;

@RequiredArgsConstructor
public final class ScopedIndexer {

    private static final int NONE = -1;

    public static final ScopedIndexer EMPTY = new ScopedIndexer(null, new Entry[0]);

    @Nullable
    private final ScopedIndexer upper;

    private final Entry[] entries;

    public record Entry(String name, int index) {
    }

    public int lookup(final String name) {
        for (var e : this.entries) {
            if (e.name.equals(name)) {
                return e.index;
            }
        }
        return NONE;
    }

    public int lookupWithUpper(final String name) {
        var index = lookup(name);
        if (index != NONE) {
            return index;
        }
        if (this.upper != null) {
            return upper.lookupWithUpper(name);
        }
        return NONE;
    }

    public void each(ObjIntConsumer<String> action) {
        for (var e : this.entries) {
            action.accept(e.name, e.index);
        }
    }

    public String name(int index) {
        return this.entries[index].name;
    }
}
