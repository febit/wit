package org.febit.wit.parser;

import org.febit.wit.runtime.heap.Heap;
import org.jspecify.annotations.Nullable;

public record VarAddress(
        Kind kind,
        int frameOffset,
        int index,
        @Nullable Heap heap,
        @Nullable String key,
        @Nullable Object value
) {

    public enum Kind {
        VAR,
        FRAME_VAR,
        DIRECT,
        HEAP,
        ;
    }

    static VarAddress ofVariable(int index) {
        return new VarAddress(Kind.VAR, 0, index, null, null, null);
    }

    static VarAddress ofUpper(int frameOffset, int index) {
        return new VarAddress(Kind.FRAME_VAR, frameOffset, index, null, null, null);
    }

    static VarAddress ofHeap(Heap heap, String name) {
        return new VarAddress(Kind.HEAP, -1, -1, heap, name, null);
    }

    static VarAddress ofDirect(@Nullable Object value) {
        return new VarAddress(Kind.DIRECT, -1, -1, null, null, value);
    }

}
