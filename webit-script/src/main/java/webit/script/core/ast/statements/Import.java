// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core.ast.statements;

import webit.script.Context;
import webit.script.Template;
import webit.script.core.VariantIndexer;
import webit.script.core.ast.Expression;
import webit.script.core.ast.ResetableValueExpression;
import webit.script.util.StatementUtil;

/**
 *
 * @author Zqq
 */
public final class Import extends AbstractInclude {

    private final String[] exportNames;
    private final ResetableValueExpression[] toResetableValues;
    private final boolean exportAll;

    public Import(Expression templateNameExpr, Expression paramsExpr, String[] exportNames, ResetableValueExpression[] toContextValues, Template template, int line, int column) {
        super(templateNameExpr, paramsExpr, template, line, column);
        if (exportNames == null || exportNames.length == 0) {
            this.exportNames = null;
            this.toResetableValues = null;
            this.exportAll = true;
        } else {
            this.exportNames = exportNames;
            this.toResetableValues = toContextValues;
            this.exportAll = false;
        }
    }

    public Object execute(final Context context) {
        final Context childContext = mergeTemplate(context);
        if (exportAll) {
            final VariantIndexer destIndexer = context.indexers[context.indexer];
            if (context.indexer == destIndexer.id) {

                final Object[] destValues = context.vars;

                final VariantIndexer srcIndexer = childContext.indexers[0];
                final String[] srcNames = srcIndexer.names;
                final int[] srcIndexs = srcIndexer.indexs;
                final Object[] srcValues = childContext.vars;
                for (int i = 0, len = srcNames.length; i < len; i++) {
                    int index = destIndexer.getCurrentIndex(srcNames[i]);
                    if (index >= 0) {
                        destValues[index] = srcValues[srcIndexs[i]];
                    }
                }
            }
        } else {
            final Object[] vars = childContext.get(exportNames);
            final ResetableValueExpression[] myToResetableValues = this.toResetableValues;
            for (int i = 0, len = vars.length; i < len; i++) {
                StatementUtil.executeSetValue(myToResetableValues[i], context, vars[i]);
            }
        }
        return null;
    }
}
