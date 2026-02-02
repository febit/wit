// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast;

public interface IBlock extends Statement {

    int frame();

    Statement[] statements();

    boolean hasLoopFlags();
}
