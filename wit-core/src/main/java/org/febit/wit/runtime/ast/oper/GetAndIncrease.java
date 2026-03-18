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
package org.febit.wit.runtime.ast.oper;

import org.febit.wit.exception.ScriptEvaluateException;
import org.febit.wit.runtime.ALU;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.ast.AssignableExpression;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.ast.Position;
import org.jspecify.annotations.Nullable;

public record GetAndIncrease(
        AssignableExpression target,
        Position position
) implements Expression {

    @Override
    @Nullable
    public Object execute(InternalContext context) {
        var targetObj = this.target;
        try {
            var value = targetObj.execute(context);
            targetObj.assign(context, ALU.plusOne(value));
            return value;
        } catch (Exception e) {
            throw ScriptEvaluateException.from(e, this);
        }
    }
}
