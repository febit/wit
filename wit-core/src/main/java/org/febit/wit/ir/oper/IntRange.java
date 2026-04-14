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
import org.febit.wit.ir.support.ALU;
import org.febit.wit.runtime.RuntimeContext;
import org.febit.wit.runtime.iter.CountDownIter;
import org.febit.wit.runtime.iter.CountUpIter;

public record IntRange(
        Expression from,
        Expression to,
        Position position
) implements Expression {

    @Override
    public Object execute(RuntimeContext context) {
        var fromObj = ALU.requireNumber(from.execute(context)).intValue();
        var toObj = ALU.requireNumber(to.execute(context)).intValue();
        return fromObj < toObj
                ? CountUpIter.of(fromObj, toObj)
                : CountDownIter.of(fromObj, toObj);
    }
}
