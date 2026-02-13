// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime;

import java.util.List;

public interface Loopable {

    /**
     * Collect loop flags in the tree.
     */
    List<LoopFlag> collectLoopFlags();
}
