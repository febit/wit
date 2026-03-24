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
package org.febit.wit.runtime.ast;

import lombok.experimental.UtilityClass;
import org.febit.wit.exception.ScriptParseException;
import org.febit.wit.runtime.ALU;
import org.febit.wit.runtime.ast.expr.DirectValue;
import org.febit.wit.runtime.ast.statement.NoopStatement;
import org.jspecify.annotations.Nullable;

@UtilityClass
public class StatementUtils {

    public static boolean isImmutableDirectValue(Expression expr) {
        return (expr instanceof DirectValue direct)
                && ALU.isKnownBaseImmutable(direct.value());
    }

    @Nullable
    public static Object evalAsConst(Expression expr) {
        return optimize(expr)
                .evalAsConst();
    }

    public static @Nullable Object[] evalConstArray(Expression[] expressions) {
        var len = expressions.length;
        var results = new Object[len];
        for (int i = 0; i < len; i++) {
            results[i] = StatementUtils.evalAsConst(expressions[i]);
        }
        return results;
    }

    public static Expression optimize(Expression expression) {
        return expression.optimize();
    }

    public static void optimize(Expression[] expression) {
        for (int i = 0; i < expression.length; i++) {
            expression[i] = optimize(expression[i]);
        }
    }

    public static Statement optimize(@Nullable Statement statement) {
        if (statement == null) {
            return NoopStatement.INSTANCE;
        }
        try {
            return statement.optimize();
        } catch (Exception e) {
            throw new ScriptParseException("Exception occur when do optimization", e, statement.position());
        }
    }

}
