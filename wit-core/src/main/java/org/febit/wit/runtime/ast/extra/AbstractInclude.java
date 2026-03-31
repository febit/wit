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
package org.febit.wit.runtime.ast.extra;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.febit.wit.Context;
import org.febit.wit.Feature;
import org.febit.wit.Vars;
import org.febit.wit.exception.NoSuchSourceException;
import org.febit.wit.exception.ScriptEvaluateException;
import org.febit.wit.exception.ScriptParseException;
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

    protected Vars extractParams(InternalContext context) {
        final Vars inputs;
        final Object paramsObj = this.params == null ? null
                : this.params.execute(context);
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

        Context merged;
        try {
            Vars inputs = extractParams(context);
            var script = context.script()
                    .engine()
                    .script(refer, String.valueOf(scriptPath));
            merged = script.merge(context, inputs);
        } catch (NoSuchSourceException | ScriptEvaluateException | ScriptParseException e) {
            throw new ScriptEvaluateException(e, this);
        }
        if (!export) {
            return Map.of();
        }
        var result = new HashMap<String, @Nullable Object>();
        merged.variables().each(result::put);
        return result;
    }
}
