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

import java.util.function.BinaryOperator;

public record CompoundAssign(
        AssignableExpression target,
        Expression delta,
        BinaryOperator<@Nullable Object> operator,
        Position position
) implements Expression {

    @Override
    @Nullable
    public Object execute(RuntimeContext context) {
        try {
            var targetObj = this.target;
            // Must execute right expr first!
            var deltaObj = delta.execute(context);
            return targetObj.assign(context,
                    operator.apply(targetObj.execute(context), deltaObj)
            );
        } catch (Exception e) {
            throw ScriptEvaluateException.from(e, this);
        }
    }

}
