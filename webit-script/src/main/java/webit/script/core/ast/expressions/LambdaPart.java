// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core.ast.expressions;

import webit.script.core.VariantManager;
import webit.script.core.ast.Expression;
import webit.script.core.ast.StatementList;
import webit.script.core.ast.statements.Return;

/**
 *
 * @author zqq
 */
public class LambdaPart extends FunctionDeclarePart{

    public LambdaPart(VariantManager varmgr, int line, int column) {
        super(varmgr, line, column);
    }
    
    public Expression pop(Expression expr) {
        return pop(toStatementList(expr));
    }
    
    public FunctionDeclare popFunctionDeclare(Expression expr) {
        return popFunctionDeclare(toStatementList(expr));
    }
    
    protected StatementList toStatementList(Expression expr){
        StatementList statementList = new StatementList();
        statementList.add(new Return(expr, expr.getLine(), expr.getColumn()));
        return statementList;
    }
}
