// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.core.ast.statements;

import org.febit.wit.InternalContext;
import org.febit.wit.core.ast.Statement;
import org.febit.wit.util.StatementUtil;

/**
 *
 * @author zqq90
 */
public class StatementGroup extends Statement {

    private final Statement[] list;

    public StatementGroup(Statement[] list, int line, int column) {
        super(line, column);
        this.list = list;
    }

    public Statement[] getList() {
        return list;
    }

    @Override
    public Object execute(InternalContext context) {
        StatementUtil.execute(this.list, context);
        return null;
    }

    @Override
    public Statement optimize() {
        if (this.list.length == 0) {
            return NoneStatement.INSTANCE;
        }
        return this;
    }
}
