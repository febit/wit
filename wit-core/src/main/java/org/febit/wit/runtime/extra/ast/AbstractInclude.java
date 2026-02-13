// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.extra.ast;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.febit.wit.Feature;
import org.febit.wit.Vars;
import org.febit.wit.exceptions.ParseException;
import org.febit.wit.exceptions.ResourceNotFoundException;
import org.febit.wit.exceptions.ScriptRuntimeException;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.Position;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.ast.Statement;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@Accessors(fluent = true)
@RequiredArgsConstructor
public abstract class AbstractInclude implements Statement {

    private final Expression pathExpr;
    @Nullable
    private final Expression paramsExpr;
    private final String refer;
    @Getter
    private final Position position;

    protected Vars prepareParams(InternalContext context) {
        final Vars params;
        final Object paramsRaw = paramsExpr == null ? null : paramsExpr.execute(context);
        if (paramsRaw == null) {
            params = Vars.empty();
        } else if (paramsRaw instanceof Map) {
            params = Vars.of((Map<?, ?>) paramsRaw);
        } else {
            throw new ScriptRuntimeException("Template param must be a Map.", paramsExpr);
        }
        return context.isEnabled(Feature.SHARE_ROOT_PARAMS)
                ? Vars.of(context.inputs(), params)
                : params;
    }

    protected Map<String, @Nullable Object> mergeTemplate(InternalContext context, boolean export) {
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
                var result = new HashMap<String, @Nullable Object>();
                newContext.heap().exportTo(result);
                return result;
            }
            return Map.of();
        } catch (ResourceNotFoundException | ScriptRuntimeException | ParseException e) {
            throw new ScriptRuntimeException(e, this);
        }
    }
}
