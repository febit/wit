// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast;

import java.util.function.Consumer;

public interface WithFlowControl {

    /**
     * Collect flow controls in current node.
     */
    void collectFlowControls(Consumer<FlowControl> collector);
}
