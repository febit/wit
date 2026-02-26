// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast.extra;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.febit.wit.Feature;
import org.febit.wit.Vars;
import org.febit.wit.exception.NoSuchSourceException;
import org.febit.wit.exception.ParseException;
import org.febit.wit.exception.ScriptEvaluateException;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.ast.Statement;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@Accessors(fluent = true)
@RequiredArgsConstructor
public abstract class AbstractInclude implements Statement {

    private final Expression path;
    @Nullable
    private final Expression params;
    private final String refer;
    @Getter
    private final Position position;

    protected Vars prepareParams(InternalContext context) {
        final Vars inputs;
        final Object paramsObj = this.params == null ? null : this.params.execute(context);
        if (paramsObj == null) {
            inputs = Vars.empty();
        } else if (paramsObj instanceof Map) {
            inputs = Vars.of((Map<?, ?>) paramsObj);
        } else {
            throw new ScriptEvaluateException("Script param must be a Map.", this.params);
        }
        return context.isEnabled(Feature.SHARE_ROOT_PARAMS)
                ? Vars.concat(context.inputs(), inputs)
                : inputs;
    }

    protected Map<String, @Nullable Object> mergeScript(InternalContext context, boolean export) {
        var scriptPath = path.execute(context);
        if (scriptPath == null) {
            throw new ScriptEvaluateException("Script name should not be null.", path);
        }
        try {
            Vars inputs = prepareParams(context);
            var script = context.script().engine().script(refer, String.valueOf(scriptPath));
            var newContext = script.merge(context, inputs);
            if (export) {
                var result = new HashMap<String, @Nullable Object>();
                newContext.variables().exportTo(result);
                return result;
            }
            return Map.of();
        } catch (NoSuchSourceException | ScriptEvaluateException | ParseException e) {
            throw new ScriptEvaluateException(e, this);
        }
    }
}
