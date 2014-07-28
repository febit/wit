/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

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
        StatementList statementList = new StatementList();
        statementList.add(new Return(expr, expr.getLine(), expr.getColumn()));
        return pop(statementList);
    }
}
