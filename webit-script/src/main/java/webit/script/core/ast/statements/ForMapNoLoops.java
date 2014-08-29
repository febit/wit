// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core.ast.statements;

import webit.script.Context;
import webit.script.core.VariantIndexer;
import webit.script.core.ast.Expression;
import webit.script.core.ast.Statement;
import webit.script.core.ast.expressions.FunctionDeclare;
import webit.script.lang.KeyIter;
import webit.script.lang.iter.KeyIterMethodFilter;
import webit.script.util.CollectionUtil;
import webit.script.util.StatementUtil;

/**
 *
 * @author Zqq
 */
public final class ForMapNoLoops extends Statement {

    protected final FunctionDeclare functionDeclareExpr;
    private final Expression mapExpr;
    private final VariantIndexer varIndexer;
    private final Statement[] statements;
    private final Statement elseStatement;

    public ForMapNoLoops(FunctionDeclare functionDeclareExpr, Expression mapExpr, VariantIndexer varIndexer, Statement[] statements, Statement elseStatement, int line, int column) {
        super(line, column);
        this.functionDeclareExpr = functionDeclareExpr;
        this.mapExpr = mapExpr;
        this.varIndexer = varIndexer;
        this.statements = statements;
        this.elseStatement = elseStatement;
    }

    @SuppressWarnings("unchecked")
    public Object execute(final Context context) {
        KeyIter iter = CollectionUtil.toKeyIter(StatementUtil.execute(mapExpr, context));
        if (iter != null && functionDeclareExpr != null) {
            iter = new KeyIterMethodFilter(context,
                    functionDeclareExpr.execute(context),
                    iter);
        }
        if (iter != null && iter.hasNext()) {
            context.push(varIndexer);
            context.set(0, iter);
            do {
                context.resetForForMap(iter.next(), iter.value());
                StatementUtil.executeInverted(this.statements, context);
            } while (iter.hasNext());
            context.pop();
            return null;
        } else if (elseStatement != null) {
            StatementUtil.execute(elseStatement, context);
        }
        return null;
    }
}
