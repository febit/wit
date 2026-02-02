// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.ast;

public interface IBlock extends Statement {

    int varIndexer();

    Statement[] statements();

    boolean hasLoops();
}
