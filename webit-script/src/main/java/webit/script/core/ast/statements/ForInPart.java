// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core.ast.statements;

import webit.script.core.VariantManager;
import webit.script.core.ast.Expression;
import webit.script.core.ast.Statement;
import webit.script.core.ast.StatementList;
import webit.script.core.ast.expressions.FunctionDeclare;
import webit.script.core.ast.expressions.LambdaPart;
import webit.script.exceptions.ParseException;
import webit.script.util.StatementUtil;

/**
 *
 * @author Zqq
 */
public final class ForInPart extends AbstractForInPart {

    private Expression collectionExpr;
    private FunctionDeclare functionDeclareExpr;

    public ForInPart(String item, Expression collectionExpr, VariantManager varmgr, int line, int column) {
        super(varmgr, line, column);
        this.collectionExpr = collectionExpr;
        if (varmgr.assignVariant(item, line, column) != 1) {
            throw new ParseException("assignVariant failed!");
        }
    }

    public ForInPart(String item, FunctionDeclare functionDeclareExpr, VariantManager varmgr, int line, int column) {
        super(varmgr, line, column);
        this.functionDeclareExpr = functionDeclareExpr;
        if (varmgr.assignVariant(item, line, column) != 1) {
            throw new ParseException("assignVariant failed!");
        }
    }

    public ForInPart setCollectionExpr(Expression collectionExpr) {
        this.collectionExpr = collectionExpr;
        return this;
    }
    
    @Override
    public Statement pop(int label) {
        if (bodyStatement.hasLoops()) {
            return new ForIn(functionDeclareExpr, collectionExpr, bodyStatement.getVarMap(), bodyStatement.getStatements(),
                    StatementUtil.collectPossibleLoopsInfoForWhile(bodyStatement, elseStatement, label),
                    elseStatement, label, line, column);
        } else {
            return new ForInNoLoops(functionDeclareExpr, collectionExpr, bodyStatement.getVarMap(), bodyStatement.getStatements(), elseStatement, line, column);
        }
    }
}
