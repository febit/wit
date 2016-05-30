// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.core.ast.statements;

import webit.script.Context;
import webit.script.core.ast.Optimizable;
import webit.script.core.ast.Statement;
import webit.script.util.StatementUtil;

/**
 *
 * @author zqq90
 */
public class StatementGroup extends Statement implements Optimizable {

    private final Statement[] list;

    public StatementGroup(Statement[] list, int line, int column) {
        super(line, column);
        this.list = list;
    }

    public Statement[] getList() {
        return list;
    }

    @Override
    public Object execute(Context context) {
        StatementUtil.execute(this.list, context);
        return null;
    }

    @Override
    public Statement optimize() throws Exception {
        if (this.list.length == 0) {
            return NoneStatement.INSTANCE;
        }
        return this;
    }
}
