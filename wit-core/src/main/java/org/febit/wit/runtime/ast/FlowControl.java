// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast;

public record FlowControl(
        Kind kind,
        int label,
        Position position
) {

    public enum Kind {
        NOOP,
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

        public boolean isNoop() {
            return this == NOOP;
        }
    }

    public boolean matchesLabel(int label) {
        return this.label == 0 || this.label == label;
    }
}
