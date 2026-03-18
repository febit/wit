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

import org.febit.wit.runtime.ALU;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.ast.StatementUtils;
import org.febit.wit.runtime.ast.expr.DirectValue;
import org.jspecify.annotations.Nullable;

public record And(
        Expression left,
        Expression right,
        Position position
) implements Expression {

    @Override
    @Nullable
    public Object execute(InternalContext context) {
        var leftObj = left.execute(context);
        return ALU.isTruly(leftObj)
                ? right.execute(context)
                : leftObj;
    }

    @Override
    public Expression optimize() {
        if (!StatementUtils.isImmutableDirectValue(left)) {
            return this;
        }
        if (StatementUtils.isImmutableDirectValue(right)) {
            return new DirectValue(
                    ALU.and(((DirectValue) left).value(), ((DirectValue) right).value()),
                    position);
        } else {
            return ALU.not(((DirectValue) left).value())
                    ? left : right;
        }
    }
}
