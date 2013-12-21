// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.statements;

import webit.script.Context;
import webit.script.core.VariantIndexer;
import webit.script.core.ast.AbstractStatement;
import webit.script.core.ast.Expression;
import webit.script.core.ast.Statement;
import webit.script.core.runtime.VariantStack;
import webit.script.util.CollectionUtil;
import webit.script.util.StatementUtil;
import webit.script.util.collection.Iter;

/**
 *
 * @author Zqq
 */
public final class ForInNoLoops extends AbstractStatement {

    private final Expression collectionExpr;
    private final VariantIndexer varIndexer;
    private final Statement[] statements;
    private final Statement elseStatement;

    public ForInNoLoops(Expression collectionExpr, VariantIndexer varIndexer, Statement[] statements, Statement elseStatement, int line, int column) {
        super(line, column);
        this.collectionExpr = collectionExpr;
        this.varIndexer = varIndexer;
        this.statements = statements;
        this.elseStatement = elseStatement;
    }

    public Object execute(final Context context) {
        final Iter iter;

        if ((iter = CollectionUtil.toIter(
                StatementUtil.execute(collectionExpr, context))) != null
                && iter.hasNext()) {

            final Statement[] statements = this.statements;
            final VariantStack vars;
            (vars = context.vars).push(varIndexer);
            vars.set(0, iter);
            do {
                vars.resetForForIn(iter.next());
                StatementUtil.executeInverted(statements, context);
            } while (iter.hasNext());
            vars.pop();
            return null;
        } else if (elseStatement != null) {
            StatementUtil.execute(elseStatement, context);
        }
        return null;
    }
}
