package org.febit.wit.parser;

import org.febit.wit.runtime.heap.Heap;
import org.jspecify.annotations.Nullable;

public record VarAddress(
        Kind kind,
        int pageOffset,
        int index,
        @Nullable Heap heap,
        @Nullable String key,
        @Nullable Object value
) {

    public enum Kind {
        CONTEXT,
        UPSTREAM,
        CONST,
        STATIC_VAR,
        ;
    }

    static VarAddress ofContext(int index) {
        return new VarAddress(Kind.CONTEXT, 0, index, null, null, null);
    }

    static VarAddress ofHeap(Heap heap, String name) {
        return new VarAddress(Kind.STATIC_VAR, -1, -1, heap, name, null);
    }

    static VarAddress ofConst(@Nullable Object value) {
        return new VarAddress(Kind.CONST, -1, -1, null, null, value);
    }

    static VarAddress ofUpstream(int pageOffset, int index) {
        return new VarAddress(Kind.UPSTREAM, pageOffset, index, null, null, null);
    }

}
