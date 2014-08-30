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
    private final int indexer;
    private final Statement[] statements;
    private final Statement elseStatement;
    private final int iterIndex;
    private final int keyIndex;
    private final int valueIndex;

    public ForMapNoLoops(FunctionDeclare functionDeclareExpr, Expression mapExpr, int indexer, int iterIndex, int keyIndex, int valueIndex, Statement[] statements, Statement elseStatement, int line, int column) {
        super(line, column);
        this.functionDeclareExpr = functionDeclareExpr;
        this.mapExpr = mapExpr;
        this.indexer = indexer;
        this.statements = statements;
        this.elseStatement = elseStatement;
        this.iterIndex = iterIndex;
        this.keyIndex = keyIndex;
        this.valueIndex = valueIndex;
    }

    @SuppressWarnings("unchecked")
    public Object execute(final Context context) {
        KeyIter iter = CollectionUtil.toKeyIter(StatementUtil.execute(mapExpr, context));
        if (iter != null && functionDeclareExpr != null) {
            iter = new KeyIterMethodFilter(context, functionDeclareExpr.execute(context), iter);
        }
        if (iter != null && iter.hasNext()) {
        final int preIndex = context.indexer;
        context.indexer = indexer;
            final Statement[] statements = this.statements;
            final int keyIndex = this.keyIndex;
            final int valueIndex = this.valueIndex;
            final Object[] vars = context.vars;
            vars[iterIndex] = iter;
            do {
                vars[keyIndex] = iter.next();
                vars[valueIndex] = iter.value();
                StatementUtil.executeInverted(statements, context);
            } while (iter.hasNext());
            context.indexer = preIndex;
            return null;
        } else if (elseStatement != null) {
            StatementUtil.execute(elseStatement, context);
        }
        return null;
    }
}
