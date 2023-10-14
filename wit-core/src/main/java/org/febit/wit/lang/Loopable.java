// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang;

import java.util.List;

/**
 * @author zqq90
 */
public interface Loopable {

    /**
     * Collect possible loops.
     *
     * @return loops list, must not null.
     */
    List<LoopMeta> collectPossibleLoops();
}
