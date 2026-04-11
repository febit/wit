/*
 * Copyright 2013-present febit.org (support@febit.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.febit.wit.ir.include;

import org.febit.wit.Context;
import org.febit.wit.Feature;
import org.febit.wit.Vars;
import org.febit.wit.exception.NoSuchSourceException;
import org.febit.wit.exception.ScriptEvaluateException;
import org.febit.wit.exception.ScriptParseException;
import org.febit.wit.ir.Expression;
import org.febit.wit.ir.Position;
import org.febit.wit.ir.Statement;
import org.febit.wit.runtime.RuntimeContext;
import org.jspecify.annotations.Nullable;

import java.util.Map;

public record Include(
        String refer,
        Expression path,
        IncludeHandler handler,
        @Nullable Expression inputs,
        Position position
) implements Statement {

    @Override
    @Nullable
    public Object execute(RuntimeContext context) {
        var included = include(context);
        handler.process(context, included);
        return null;
    }

    private Context include(RuntimeContext context) {
        var scriptPath = path.execute(context);
        if (scriptPath == null) {
            throw new ScriptEvaluateException("Script path should not be null.", path);
        }
        try {
            var inputs = resolveInputs(context);
            var script = context.script()
                    .engine()
                    .script(refer, String.valueOf(scriptPath));
            return script.eval(
                    inputs,
                    context.out(),
                    context.local(),
                    context.breakpointHandler()
            );
        } catch (NoSuchSourceException | ScriptEvaluateException | ScriptParseException e) {
            throw new ScriptEvaluateException(e, this);
        }
    }

    private Vars resolveInputs(RuntimeContext context) {
        var paramsObj = this.inputs == null ? null
                : this.inputs.execute(context);

        final Vars inputVars;
        if (paramsObj == null) {
            inputVars = Vars.empty();
        } else if (paramsObj instanceof Map) {
            inputVars = Vars.of((Map<?, ?>) paramsObj);
        } else {
            throw new ScriptEvaluateException("Script inputs must be a Map.", this.inputs);
        }
        return context.isEnabled(Feature.SHARE_ROOT_PARAMS)
                ? Vars.concat(context.inputs(), inputVars)
                : inputVars;
    }

}
