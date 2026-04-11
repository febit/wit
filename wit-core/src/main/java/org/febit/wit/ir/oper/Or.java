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

import org.febit.wit.ir.Expression;
import org.febit.wit.ir.Position;
import org.febit.wit.ir.expr.DirectValue;
import org.febit.wit.ir.support.ALU;
import org.febit.wit.ir.support.StatementUtils;
import org.febit.wit.runtime.RuntimeContext;
import org.jspecify.annotations.Nullable;

public record Or(
        Expression left,
        Expression right,
        Position position
) implements Expression {

    @Override
    @Nullable
    public Object execute(RuntimeContext context) {
        var leftObj = left.execute(context);
        return ALU.isTruly(leftObj)
                ? leftObj
                : right.execute(context);
    }

    @Override
    public Expression optimize() {
        if (!StatementUtils.isImmutableDirectValue(left)) {
            return this;
        }
        if (StatementUtils.isImmutableDirectValue(right)) {
            return new DirectValue(
                    ALU.or(((DirectValue) left).value(), ((DirectValue) right).value()),
                    position);
        } else {
            return ALU.isTruly(((DirectValue) left).value())
                    ? left : right;
        }
    }
}
