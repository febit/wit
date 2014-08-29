// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core.ast.statements;

import webit.script.Context;
import webit.script.core.VariantIndexer;
import webit.script.core.ast.Expression;
import webit.script.core.ast.Statement;
import webit.script.core.ast.expressions.FunctionDeclare;
import webit.script.lang.Iter;
import webit.script.lang.iter.IterMethodFilter;
import webit.script.util.CollectionUtil;
import webit.script.util.StatementUtil;

/**
 *
 * @author Zqq
 */
public final class ForInNoLoops extends Statement {

    private final Expression collectionExpr;
    private final VariantIndexer varIndexer;
    private final Statement[] statements;
    private final Statement elseStatement;
    protected final FunctionDeclare functionDeclareExpr;

    public ForInNoLoops(FunctionDeclare functionDeclareExpr, Expression collectionExpr, VariantIndexer varIndexer, Statement[] statements, Statement elseStatement, int line, int column) {
        super(line, column);
        this.functionDeclareExpr = functionDeclareExpr;
        this.collectionExpr = collectionExpr;
        this.varIndexer = varIndexer;
        this.statements = statements;
        this.elseStatement = elseStatement;
    }

    public Object execute(final Context context) {
        Iter iter = CollectionUtil.toIter(StatementUtil.execute(collectionExpr, context));
        if (iter != null && functionDeclareExpr != null) {
            iter = new IterMethodFilter(context,
                    functionDeclareExpr.execute(context),
                    iter);
        }
        if (iter != null
                && iter.hasNext()) {
            final Statement[] statements = this.statements;
            context.push(varIndexer);
            context.set(0, iter);
            do {
                context.resetForForIn(iter.next());
                StatementUtil.executeInverted(statements, context);
            } while (iter.hasNext());
            context.pop();
            return null;
        } else if (elseStatement != null) {
            StatementUtil.execute(elseStatement, context);
        }
        return null;
    }
}
