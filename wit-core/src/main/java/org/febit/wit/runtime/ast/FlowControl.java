// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast;

import org.febit.wit.runtime.FlowState;

public record FlowControl(
        int label,
        FlowState state,
        Position position
) {

    public boolean matchesLabel(int label) {
        return this.label == 0 || this.label == label;
    }
}
