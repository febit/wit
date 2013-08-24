// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.statments;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import webit.script.Context;
import webit.script.Template;
import webit.script.core.ast.AbstractStatment;
import webit.script.core.ast.Expression;
import webit.script.core.ast.expressions.ContextValue;
import webit.script.exceptions.ScriptRuntimeException;
import webit.script.util.StatmentUtil;

/**
 *
 * @author Zqq
 */
public class ImportStatment extends AbstractStatment {

    private final Expression templateNameExpr;
    private final Expression paramsExpr;
    private final String[] exportNames;
    private final ContextValue[] toContextValues;
    private final boolean exportAll;

    public ImportStatment(Expression templateNameExpr, Expression paramsExpr, String[] exportNames, ContextValue[] toContextValues, int line, int column) {
        super(line, column);
        this.templateNameExpr = templateNameExpr;
        this.paramsExpr = paramsExpr;
        if (exportNames == null || exportNames.length == 0) {
            this.exportNames = null;
            this.toContextValues = null;
            this.exportAll = true;
        } else {
            this.exportNames = exportNames;
            this.toContextValues = toContextValues;
            this.exportAll = false;
        }

    }

    @Override
    public void execute(Context context) {
        Object templateNameObject = StatmentUtil.execute(templateNameExpr, context);

        if (templateNameObject != null) {
            String templateName = String.valueOf(templateNameObject);
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
            Template thisTemplate = context.template;

            Template childTemplate;

            try {
                childTemplate = thisTemplate.engine.getTemplate(thisTemplate.name, templateName);
                Context childContext = childTemplate.merge(params, context.getOut());
                if (exportAll) {
                    
                Map returns = new HashMap();
                childContext.exportTo(returns);
                context.vars.setToCurrentContext(returns);
                }else{
                    Object[] vars = childContext.vars.get(exportNames);
                    for (int i = 0; i < vars.length; i++) {
                        Object object = vars[i];
                        toContextValues[i].set(context,vars[i]);
                    }
                }

            } catch (Exception e) {
                throw new ScriptRuntimeException(e);
            }
        }
    }
}
