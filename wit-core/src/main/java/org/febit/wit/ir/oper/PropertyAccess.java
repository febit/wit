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
package org.febit.wit.ir.oper;

import org.febit.wit.exception.ScriptEvaluateException;
import org.febit.wit.ir.AssignableExpression;
import org.febit.wit.ir.Expression;
import org.febit.wit.ir.Position;
import org.febit.wit.runtime.RuntimeContext;
import org.jspecify.annotations.Nullable;

public record PropertyAccess(
        Expression target,
        Expression property,
        Position position
) implements AssignableExpression {

    @Override
    @Nullable
    public Object execute(RuntimeContext context) {
        try {
            return context.getProperty(
                    target.execute(context),
                    property.execute(context)
            );
        } catch (Exception e) {
            throw ScriptEvaluateException.from(e, this);
        }
    }

    @Override
    @Nullable
    public Object assign(RuntimeContext context, @Nullable final Object value) {
        try {
            context.setProperty(
                    target.execute(context),
                    property.execute(context),
                    value
            );
            return value;
        } catch (Exception e) {
            throw ScriptEvaluateException.from(e, this);
        }
    }
}
