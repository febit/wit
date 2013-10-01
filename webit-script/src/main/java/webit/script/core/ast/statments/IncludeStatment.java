// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.statments;

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
public final class IncludeStatment extends AbstractStatment {

    private final Expression templateNameExpr;
    private final Expression paramsExpr;

    public IncludeStatment(Expression templateNameExpr, Expression paramsExpr, int line, int column) {
        super(line, column);
        this.templateNameExpr = templateNameExpr;
        this.paramsExpr = paramsExpr;
    }

    @SuppressWarnings("unchecked")
    public Object execute(final Context context) {
        final Object templateNameObject = StatmentUtil.execute(templateNameExpr, context);

        if (templateNameObject != null) {
            Map params = null;
            if (paramsExpr != null) {

                final Object paramsObject = StatmentUtil.execute(paramsExpr, context);
                if (paramsObject != null) {
                    if (paramsObject instanceof Map) {
                        params = (Map) paramsObject;
                    } else {
                        throw new ScriptRuntimeException("Not a Map", paramsExpr);
                    }
                }
            }
            final Template thisTemplate = context.template;

            final Template childTemplate;

            try {
                childTemplate = thisTemplate.engine.getTemplate(thisTemplate.name, String.valueOf(templateNameObject));
                childTemplate.merge(params, context.getOut());
            } catch (Throwable e) {
                throw new ScriptRuntimeException(e);
            }
        }
        return null;
    }
}
