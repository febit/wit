// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast;

import org.febit.wit.runtime.ast.statement.StatementBatch;

import java.util.List;

public interface IBlock extends Statement, WithFlowControl {

    int frame();

    List<StatementBatch> body();
}
