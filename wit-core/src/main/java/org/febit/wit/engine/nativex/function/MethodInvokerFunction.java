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
package org.febit.wit.engine.nativex.function;

import org.febit.wit.engine.WitFunction;
import org.febit.wit.engine.nativex.support.MethodInvoker;
import org.febit.wit.exception.ScriptEvaluateException;
import org.febit.wit.runtime.Undefined;
import org.jspecify.annotations.Nullable;

public record MethodInvokerFunction(
        MethodInvoker invoker
) implements WitFunction.Constable {

    @Override
    public Object apply(@Nullable Object @Nullable [] args) {
        try {
            var result = invoker.invoke(args);
            return invoker.returnsVoid()
                    ? Undefined.UNDEFINED
                    : result;
        } catch (Throwable e) {
            throw new ScriptEvaluateException("Cannot invoke method", e);
        }
    }
}
