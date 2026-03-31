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

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.ast.AssignableExpression;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.iter.Iters;

@Accessors(fluent = true)
@RequiredArgsConstructor
public final class GroupAssign implements Expression {

    private final AssignableExpression[] targets;
    private final Expression value;
    @Getter
    private final Position position;

    @Override
    public Object execute(InternalContext context) {
        var values = value.execute(context);
        var iter = Iters.ofIter(values, this);
        var targetsObj = this.targets;

        final int targetSize = targetsObj.length;
        var results = new Object[targetSize];
        int current = 0;
        while (iter.hasNext() && current < targetSize) {
            Object next = iter.next();
            results[current] = targetsObj[current].assign(context, next);
            current++;
        }
        for (; current < targetSize; current++) {
            targetsObj[current].assign(context, null);
            results[current] = null;
        }
        return results;
    }
}
