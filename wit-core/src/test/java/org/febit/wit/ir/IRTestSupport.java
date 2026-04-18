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
package org.febit.wit.ir;

import lombok.experimental.UtilityClass;
import org.febit.wit.engine.WitFunction;
import org.febit.wit.ir.expr.ConstantValue;
import org.febit.wit.runtime.RuntimeContext;
import org.febit.wit.runtime.Undefined;
import org.jspecify.annotations.Nullable;

import java.util.stream.Stream;

import static org.mockito.Mockito.mock;

@UtilityClass
public class IRTestSupport {

    public static final Position DUMMY_POS = TextPosition.UNKNOWN;
    public static final RuntimeContext DUMMY_CONTEXT = mock(RuntimeContext.class);
    public static final Undefined UNDEFINED = Undefined.UNDEFINED;

    public static ConstantValue constant(Object value) {
        return new ConstantValue(value, DUMMY_POS);
    }

    public static ExpressionArray expressions(Object... values) {
        return ExpressionArray.of(Stream.of(values)
                .map(IRTestSupport::constant)
                .map(Expression.class::cast)
                .toList());
    }

    public static Object[] args(@Nullable Object... args) {
        return args;
    }

    public static WitFunction func(WitFunction lambda) {
        return lambda;
    }

    public static WitFunction.Constable func(WitFunction.Constable lambda) {
        return lambda;
    }
}
