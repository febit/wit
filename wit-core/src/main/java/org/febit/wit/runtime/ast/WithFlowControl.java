// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast;

import java.util.function.Consumer;

public interface WithFlowControl {

    /**
     * Collect flow controls that may bubble up to parent statement, such as break/continue/return.
     * Without controls swallowed by self.
     */
    void bubbleFlowControls(Consumer<FlowControl> collector);
}
