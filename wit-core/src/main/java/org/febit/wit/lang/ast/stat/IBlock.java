// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.ast.stat;

import org.febit.wit.lang.ast.Statement;

/**
 * @author zqq90
 */
public interface IBlock extends Statement {

    int getVarIndexer();

    Statement[] getStatements();

    boolean hasLoops();
}
