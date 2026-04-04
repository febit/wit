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

import lombok.RequiredArgsConstructor;
import org.febit.wit.runtime.RuntimeContext;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.ast.StatementUtils;
import org.jspecify.annotations.Nullable;

import java.util.List;

@RequiredArgsConstructor(staticName = "of")
public class ExpressionArray {

    private static final Expression[] EMPTY_EXPRS = new Expression[0];

    private final Expression[] values;

    public static ExpressionArray ofEmpty() {
        return new ExpressionArray(EMPTY_EXPRS);
    }

    public List<Expression> asList() {
        return List.of(values);
    }

    public @Nullable Object[] execute(RuntimeContext context) {
        var exprs = this.values;
        var len = exprs.length;
        var results = new Object[len];
        for (int i = 0; i < len; i++) {
            results[i] = exprs[i].execute(context);
        }
        return results;
    }

    public @Nullable Object[] evalAsConst() {
        var exprs = this.values;
        var len = exprs.length;
        var results = new Object[len];
        for (int i = 0; i < len; i++) {
            results[i] = StatementUtils.evalAsConst(exprs[i]);
        }
        return results;
    }
}
