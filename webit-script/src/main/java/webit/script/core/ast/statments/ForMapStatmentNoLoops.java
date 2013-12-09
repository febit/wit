// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.statments;

import java.util.Map;
import webit.script.Context;
import webit.script.core.VariantIndexer;
import webit.script.core.ast.AbstractStatment;
import webit.script.core.ast.Expression;
import webit.script.core.ast.Statment;
import webit.script.core.runtime.VariantStack;
import webit.script.exceptions.ScriptRuntimeException;
import webit.script.util.CollectionUtil;
import webit.script.util.StatmentUtil;
import webit.script.util.collection.Iter;

/**
 *
 * @author Zqq
 */
public final class ForMapStatmentNoLoops extends AbstractStatment {

    private final int iterIndex;
    private final int keyIndex;
    private final int valueIndex;
    private final Expression mapExpr;
    private final VariantIndexer varIndexer;
    private final Statment[] statments;
    private final Statment elseStatment;

    public ForMapStatmentNoLoops(int iterIndex, int keyIndex, int valueIndex, Expression mapExpr, VariantIndexer varIndexer, Statment[] statments, Statment elseStatment, int line, int column) {
        super(line, column);
        this.iterIndex = iterIndex;
        this.keyIndex = keyIndex;
        this.valueIndex = valueIndex;
        this.mapExpr = mapExpr;
        this.varIndexer = varIndexer;
        this.statments = statments;
        this.elseStatment = elseStatment;
    }

    @SuppressWarnings("unchecked")
    public Object execute(final Context context) {
        final Object object = StatmentUtil.execute(mapExpr, context);
        final Iter<Map.Entry> iter;
        if (object != null) {
            if (object instanceof Map) {
                iter = CollectionUtil.toIter(((Map) object).entrySet());
            } else {
                throw new ScriptRuntimeException("Not a instance of java.util.Map");
            }
        } else {
            iter = null;
        }
        if (iter != null && iter.hasNext()) {
            Map.Entry entry;
            final Statment[] statments = this.statments;
            final VariantStack vars;
            (vars = context.vars).push(varIndexer);
            do {
                entry = iter.next();
                vars.resetCurrentWith(iterIndex, iter, keyIndex, entry.getKey(), valueIndex, entry.getValue());
                StatmentUtil.executeInverted(statments, context);
            } while (iter.hasNext());
            vars.pop();
            return null;
        } else if (elseStatment != null) {
            StatmentUtil.execute(elseStatment, context);
        }
        return null;
    }
}
