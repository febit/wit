// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast;

public record LoopFlag(
        Kind kind,
        int label,
        Position position
) {

    public enum Kind {
        NONE,
        BREAK,
        CONTINUE,
        RETURN,
        ;

        public boolean isBreak() {
            return this == BREAK;
        }

        public boolean isReturn() {
            return this == RETURN;
        }

        public boolean isBreakOrContinue() {
            return this == BREAK || this == CONTINUE;
        }

        public boolean isNone() {
            return this == NONE;
        }
    }

    public boolean matchLabel(int label) {
        return this.label == 0 || this.label == label;
    }
}
