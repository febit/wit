// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core.ast.statements;

import webit.script.Context;
import webit.script.Template;
import webit.script.core.Variants;
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
            Variants vars = context.getCurrentVars();
            if (vars != null) {
                vars.merge(childContext.getCurrentVars());
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
