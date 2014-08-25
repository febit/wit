// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core.ast.statements;

import webit.script.Context;
import webit.script.core.VariantIndexer;
import webit.script.core.VariantStack;
import webit.script.core.ast.AbstractStatement;
import webit.script.core.ast.Expression;
import webit.script.core.ast.Statement;
import webit.script.lang.KeyIter;
import webit.script.util.CollectionUtil;
import webit.script.util.StatementUtil;

/**
 *
 * @author Zqq
 */
public final class ForMapNoLoops extends AbstractStatement {

    private final Expression mapExpr;
    private final VariantIndexer varIndexer;
    private final Statement[] statements;
    private final Statement elseStatement;

    public ForMapNoLoops( Expression mapExpr, VariantIndexer varIndexer, Statement[] statements, Statement elseStatement, int line, int column) {
        super(line, column);
        this.mapExpr = mapExpr;
        this.varIndexer = varIndexer;
        this.statements = statements;
        this.elseStatement = elseStatement;
    }

    @SuppressWarnings("unchecked")
    public Object execute(final Context context) {
        final KeyIter iter = CollectionUtil.toKeyIter(StatementUtil.execute(mapExpr, context));
        if (iter != null && iter.hasNext()) {
            final VariantStack vars;
            (vars = context.vars).push(varIndexer);
            vars.set(0, iter);
            do {
                vars.resetForForMap(iter.next(), iter.value());
                StatementUtil.executeInverted(this.statements, context);
            } while (iter.hasNext());
            vars.pop();
            return null;
        } else if (elseStatement != null) {
            StatementUtil.execute(elseStatement, context);
        }
        return null;
    }
}
