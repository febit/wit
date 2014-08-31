// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core.ast.statements;

import java.util.Map;
import webit.script.Context;
import webit.script.Engine;
import webit.script.Template;
import webit.script.core.ast.Expression;
import webit.script.core.ast.Statement;
import webit.script.exceptions.ScriptRuntimeException;
import webit.script.lang.KeyValues;
import webit.script.util.KeyValuesUtil;

/**
 *
 * @author Zqq
 */
public abstract class AbstractInclude extends Statement {

    private final Expression templateNameExpr;
    private final Expression paramsExpr;
    private final String myTemplateName;
    private final Engine engine;

    public AbstractInclude(Expression templateNameExpr, Expression paramsExpr, Template template, int line, int column) {
        super(line, column);
        this.templateNameExpr = templateNameExpr;
        this.paramsExpr = paramsExpr;
        this.myTemplateName = template.name;
        this.engine = template.engine;
    }

    @SuppressWarnings("unchecked")
    protected Context mergeTemplate(final Context context) {
        final Object templateName;
        if ((templateName = templateNameExpr.execute(context)) != null) {
            final KeyValues params;
            final Object paramsObject;
            if (paramsExpr != null
                    && (paramsObject = paramsExpr.execute(context)) != null) {
                if (paramsObject instanceof Map) {
                    params = KeyValuesUtil.wrap((Map) paramsObject);
                } else {
                    throw new ScriptRuntimeException("Template param must be a Map.", paramsExpr);
                }
            } else {
                params = KeyValuesUtil.EMPTY_KEY_VALUES;
            }
            try {
                return engine.getTemplate(myTemplateName, String.valueOf(templateName))
                        .mergeForInlude(context, params);
            } catch (Exception e) {
                throw new ScriptRuntimeException(e, this);
            }
        } else {
            throw new ScriptRuntimeException("Template name should not be null.", templateNameExpr);
        }
    }
}
