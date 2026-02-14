package org.febit.wit.core;

import org.jspecify.annotations.Nullable;

public record VarAddress(
        Kind kind,
        int pageOffset,
        int index,
        @Nullable Object constValue
) {

    public enum Kind {
        CONTEXT,
        UPSTREAM,
        CONST,
        STATIC_VAR,
        ;
    }

    static VarAddress ofContext(int index) {
        return new VarAddress(Kind.CONTEXT, 0, index, null);
    }

    static VarAddress ofStaticVar(String name) {
        return new VarAddress(Kind.STATIC_VAR, -1, -1, name);
    }

    static VarAddress ofConst(@Nullable Object value) {
        return new VarAddress(Kind.CONST, -1, -1, value);
    }

    static VarAddress ofUpstream(int pageOffset, int index) {
        return new VarAddress(Kind.UPSTREAM, pageOffset, index, null);
    }

}
