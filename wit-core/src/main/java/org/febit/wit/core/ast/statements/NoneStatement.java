// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.core.ast.statements;

import org.febit.wit.InternalContext;
import org.febit.wit.core.ast.Statement;

/**
 * @author zqq90
 */
public final class NoneStatement extends Statement {

    public static final NoneStatement INSTANCE = new NoneStatement();

    private NoneStatement() {
        this(-1, -1);
    }

    public NoneStatement(int line, int column) {
        super(line, column);
    }

    @Override
    public Object execute(final InternalContext context) {
        return null;
    }

    @Override
    public Statement optimize() {
        return null;
    }
}
