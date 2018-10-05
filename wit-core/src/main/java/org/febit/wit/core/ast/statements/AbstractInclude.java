// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.core.ast.statements;

import org.febit.wit.Context;
import org.febit.wit.InternalContext;
import org.febit.wit.Template;
import org.febit.wit.Vars;
import org.febit.wit.core.ast.Expression;
import org.febit.wit.core.ast.Statement;
import org.febit.wit.core.ast.expressions.DirectValue;
import org.febit.wit.exceptions.ParseException;
import org.febit.wit.exceptions.ResourceNotFoundException;
import org.febit.wit.exceptions.ScriptRuntimeException;
import org.febit.wit.util.StatementUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zqq90
 */
public abstract class AbstractInclude extends Statement {

    private static final Expression EXPR_EMPTY_PARAMS = new DirectValue(null, -1, -1);

    private final Expression pathExpr;
    private final Expression paramsExpr;
    private final String refer;

    public AbstractInclude(Expression pathExpr, Expression paramsExpr, String refer, int line, int column) {
        super(line, column);
        this.pathExpr = StatementUtil.optimize(pathExpr);
        this.paramsExpr = paramsExpr == null ? EXPR_EMPTY_PARAMS : StatementUtil.optimize(paramsExpr);
        this.refer = refer;
    }

    protected Vars prepareParams(final InternalContext context) {
        final Vars params;
        final Object paramsObject = paramsExpr.execute(context);
        if (paramsObject == null) {
            params = Vars.EMPTY;
        } else if (paramsObject instanceof Map) {
            params = Vars.of((Map<?, ?>) paramsObject);
        } else {
            throw new ScriptRuntimeException("Template param must be a Map.", paramsExpr);
        }
        return context.getEngine().isShareRootData() ? Vars.of(context.getRootParams(), params) : params;
    }

    protected Map<String, Object> mergeTemplate(final InternalContext context, boolean export) {
        final Object templatePath = pathExpr.execute(context);
        if (templatePath == null) {
            throw new ScriptRuntimeException("Template name should not be null.", pathExpr);
        }
        try {
            Template template = context.getEngine().getTemplate(refer, String.valueOf(templatePath));
            Context newContext = template.mergeToContext(context, prepareParams(context));
            if (export) {
                Map<String, Object> result = new HashMap<>();
                newContext.exportTo(result);
                return result;
            }
            return null;
        } catch (ResourceNotFoundException | ScriptRuntimeException | ParseException e) {
            throw new ScriptRuntimeException(e, this);
        }
    }
}
