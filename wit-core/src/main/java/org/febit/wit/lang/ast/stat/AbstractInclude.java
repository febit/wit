// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.ast.stat;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.febit.wit.Context;
import org.febit.wit.InternalContext;
import org.febit.wit.Vars;
import org.febit.wit.exceptions.ParseException;
import org.febit.wit.exceptions.ResourceNotFoundException;
import org.febit.wit.exceptions.ScriptRuntimeException;
import org.febit.wit.lang.Position;
import org.febit.wit.lang.ast.Expression;
import org.febit.wit.lang.ast.Statement;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zqq90
 */
@RequiredArgsConstructor
public abstract class AbstractInclude implements Statement {

    private final Expression pathExpr;
    @Nullable
    private final Expression paramsExpr;
    private final String refer;
    @Getter
    private final Position position;

    protected Vars prepareParams(final InternalContext context) {
        final Vars params;
        final Object paramsRaw = paramsExpr == null ? null : paramsExpr.execute(context);
        if (paramsRaw == null) {
            params = Vars.EMPTY;
        } else if (paramsRaw instanceof Map) {
            params = Vars.of((Map<?, ?>) paramsRaw);
        } else {
            throw new ScriptRuntimeException("Template param must be a Map.", paramsExpr);
        }
        return context.getEngine().isShareRootData() ? Vars.of(context.getRootParams(), params) : params;
    }

    @Nullable
    protected Map<String, Object> mergeTemplate(final InternalContext context, boolean export) {
        var templatePath = pathExpr.execute(context);
        if (templatePath == null) {
            throw new ScriptRuntimeException("Template name should not be null.", pathExpr);
        }
        try {
            var newContext = context.mergeTemplate(
                    refer,
                    String.valueOf(templatePath),
                    prepareParams(context)
            );
            if (export) {
                var result = new HashMap<String, Object>();
                newContext.exportTo(result);
                return result;
            }
            return null;
        } catch (ResourceNotFoundException | ScriptRuntimeException | ParseException e) {
            throw new ScriptRuntimeException(e, this);
        }
    }
}
