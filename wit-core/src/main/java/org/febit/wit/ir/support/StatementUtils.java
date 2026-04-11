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
package org.febit.wit.ir.support;

import lombok.experimental.UtilityClass;
import org.febit.wit.exception.ScriptParseException;
import org.febit.wit.ir.Expression;
import org.febit.wit.ir.Statement;
import org.febit.wit.ir.expr.DirectValue;
import org.febit.wit.ir.statement.NoopStatement;
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

    public static Expression optimize(Expression expression) {
        try {
            return expression.optimize();
        } catch (Exception e) {
            throw new ScriptParseException("Exception occur when do optimization", e, expression.position());
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
