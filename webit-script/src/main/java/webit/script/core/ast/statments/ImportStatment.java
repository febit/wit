// Copyright (c) 2013, Webit Team. All Rights Reserved.

package webit.script.core.ast.statments;

import java.util.HashMap;
import java.util.Map;
import webit.script.Context;
import webit.script.Template;
import webit.script.core.ast.AbstractStatment;
import webit.script.core.ast.Expression;
import webit.script.exceptions.ScriptRuntimeException;
import webit.script.util.StatmentUtil;

/**
 *
 * @author Zqq
 */
public class ImportStatment extends AbstractStatment {

    private final Expression templateNameExpr;
    private final Expression paramsExpr;

    public ImportStatment(Expression templateNameExpr, Expression paramsExpr, int line, int column) {
        super(line, column);
        this.templateNameExpr = templateNameExpr;
        this.paramsExpr = paramsExpr;
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
                Map returns = new HashMap();
                childContext.exportTo(returns);
                context.vars.setToCurrentContext(returns);

            } catch (Exception e) {
                throw new ScriptRuntimeException(e);
            }
        }
    }
}
