// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.statments;

import java.util.HashMap;
import java.util.Map;
import webit.script.Context;
import webit.script.Template;
import webit.script.core.ast.AbstractStatment;
import webit.script.core.ast.Expression;
import webit.script.core.ast.ResetableValueExpression;
import webit.script.exceptions.ScriptRuntimeException;
import webit.script.util.StatmentUtil;

/**
 *
 * @author Zqq
 */
public final class ImportStatment extends AbstractStatment {

    private final Expression templateNameExpr;
    private final Expression paramsExpr;
    private final String[] exportNames;
    private final ResetableValueExpression[] toResetableValues;
    private final boolean exportAll;

    public ImportStatment(Expression templateNameExpr, Expression paramsExpr, String[] exportNames, ResetableValueExpression[] toContextValues, int line, int column) {
        super(line, column);
        this.templateNameExpr = templateNameExpr;
        this.paramsExpr = paramsExpr;
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

    @SuppressWarnings("unchecked")
    public Object execute(final Context context) {
        final Object templateNameObject = StatmentUtil.execute(templateNameExpr, context);

        if (templateNameObject != null) {
            Map params = null;
            if (paramsExpr != null) {

                Object paramsObject = StatmentUtil.execute(paramsExpr, context);
                if (paramsObject != null) {
                    if (paramsObject instanceof Map) {
                        params = (Map) paramsObject;
                    } else {
                        throw new ScriptRuntimeException("Params must be wrapped in a Map", paramsExpr);
                    }
                }
            }
            final Template thisTemplate = context.template;

            final Template childTemplate;

            try {
                childTemplate = thisTemplate.engine.getTemplate(thisTemplate.name, String.valueOf(templateNameObject));
                final Context childContext = childTemplate.merge(params, context.getOut());
                if (exportAll) {

                    final Map returns = new HashMap();
                    childContext.exportTo(returns);
                    context.vars.setToCurrentContext(returns);
                } else {
                    final Object[] vars = childContext.vars.get(exportNames);
                    for (int i = 0; i < vars.length; i++) {
                        StatmentUtil.executeSetValue(toResetableValues[i], context, vars[i]);
                    }
                }

            } catch (Throwable e) {
                throw new ScriptRuntimeException(e);
            }
        }
        return null;
    }
}
