// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.statements;

import webit.script.Context;
import webit.script.core.ast.AbstractStatement;
import webit.script.core.ast.Statement;
import webit.script.util.StatementUtil;

/**
 *
 * @author zqq90 <zqq_90@163.com>
 */
public class StatementGroup extends AbstractStatement {

    private final Statement[] list;

    public StatementGroup(Statement[] list, int line, int column) {
        super(line, column);
        this.list = list;
    }

    public Statement[] getList() {
        return list;
    }

    public Object execute(Context context) {
        StatementUtil.execute(this.list, context);
        return null;
    }
}
