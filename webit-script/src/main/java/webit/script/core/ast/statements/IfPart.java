// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core.ast.statements;

import webit.script.core.ast.Expression;
import webit.script.core.ast.Position;
import webit.script.core.ast.Statement;
import webit.script.util.StatementUtil;

/**
 *
 * @author Zqq
 */
public final class IfPart extends Position {

    private Expression ifExpr;
    private Statement thenStatement;

    public IfPart(Expression ifExpr, Statement thenStatement, int line, int column) {
        super(line, column);
        this.ifExpr = ifExpr;
        this.thenStatement = StatementUtil.optimize(thenStatement);
    }

    public Statement pop() {
        return pop(null);
    }

    public Statement pop(Statement elseStatement) {
        elseStatement = StatementUtil.optimize(elseStatement);
        if (this.thenStatement != null) {
            if (elseStatement != null) {
                return new IfElse(ifExpr, thenStatement, elseStatement, line, column);
            } else {
                return new If(ifExpr, thenStatement, line, column);
            }
        } else if (elseStatement != null) {
            return new IfNot(ifExpr, elseStatement, line, column);
        } else {
            return NoneStatement.INSTANCE;
        }
    }
}
