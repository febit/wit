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
import org.febit.wit.engine.nativex.support.MethodMatchUtils;
import org.febit.wit.exception.ScriptEvaluateException;
import org.jspecify.annotations.Nullable;

import java.util.List;

public record MultiMethodInvokerFunction(
        List<MethodInvoker<?>> invokers,
        boolean withoutReceiver
) implements WitFunction.Constable {

    @Nullable
    @Override
    public Object apply(@Nullable Object @Nullable [] args) {
        MethodInvoker<?> invoker;
        if (withoutReceiver) {
            invoker = MethodMatchUtils.findBest(invokers, args, 0);
        } else {
            if (args == null
                    || args.length == 0
                    || args[0] == null) {
                throw new ScriptEvaluateException("this method need one argument at least");
            }
            invoker = MethodMatchUtils.findBest(invokers, args, 1);
        }
        if (invoker == null) {
            throw new ScriptEvaluateException("no such native method");
        }
        return invoker.apply(args);
    }
}
