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
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.ast.StatementUtils;
import org.febit.wit.runtime.ast.expr.DirectValue;
import org.jspecify.annotations.Nullable;

import java.util.function.UnaryOperator;

public record ConstableUnaryOperator(
        Expression target,
        UnaryOperator<@Nullable Object> operator,
        Position position
) implements Expression {

    @Override
    @Nullable
    public Object execute(InternalContext context) {
        try {
            return operator.apply(target.execute(context));
        } catch (Exception e) {
            throw ScriptEvaluateException.from(e, this);
        }
    }

    @Override
    public Expression optimize() {
        if (StatementUtils.isImmutableDirectValue(target)) {
            return new DirectValue(operator.apply(((DirectValue) target).value()), position);
        }
        return this;
    }

    @Override
    @Nullable
    public Object evalAsConst() {
        return operator.apply(StatementUtils.evalAsConst(target));
    }
}
