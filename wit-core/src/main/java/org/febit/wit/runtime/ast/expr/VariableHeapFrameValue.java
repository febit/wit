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
package org.febit.wit.runtime.ast.expr;

import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.ast.AssignableExpression;
import org.febit.wit.runtime.ast.Position;
import org.jspecify.annotations.Nullable;

public record VariableHeapFrameValue(
        int frame,
        int index,
        Position position
) implements AssignableExpression {

    @Override
    @Nullable
    public Object execute(InternalContext context) {
        return context.variables().getAtFrame(frame, index);
    }

    @Override
    @Nullable
    public Object assign(InternalContext context, @Nullable Object value) {
        context.variables().setAtFrame(frame, index, value);
        return value;
    }
}
