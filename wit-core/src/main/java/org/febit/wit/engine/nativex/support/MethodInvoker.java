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
package org.febit.wit.engine.nativex.support;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.febit.wit.engine.WitFunction;
import org.febit.wit.exception.ScriptEvaluateException;
import org.febit.wit.runtime.Undefined;
import org.jspecify.annotations.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Executable;

@Getter
@Builder(
        builderClassName = "Builder"
)
@Accessors(fluent = true)
public final class MethodInvoker<E extends Executable> implements ExecutableAware<E>, WitFunction.Constable {

    @lombok.NonNull
    @SuppressWarnings("NullableProblems")
    private final E executable;
    @lombok.NonNull
    private final MethodHandle handle;
    @lombok.NonNull
    private final Handler handler;
    private final int argsCount;
    private final boolean returnsVoid;
    private final boolean isStatic;
    @Nullable
    private final Class<?> varargsComponentType;

    @Nullable
    public Object invoke(@Nullable Object @Nullable [] args) throws Throwable {
        var fitArgs = MethodInvokerUtils.fitArgs(args, argsCount, varargsComponentType);
        return handler.invoke(handle, fitArgs);
    }

    @Nullable
    @Override
    public Object apply(@Nullable Object @Nullable [] args) {
        try {
            var result = invoke(args);
            return returnsVoid
                    ? Undefined.UNDEFINED
                    : result;
        } catch (Throwable e) {
            throw new ScriptEvaluateException("Cannot invoke method", e);
        }
    }

    @Override
    public String toString() {
        return "MethodInvoker[" +
                "executable=" + executable + ']';
    }

    @FunctionalInterface
    public interface Handler {
        @SuppressWarnings({
                "java:S112", // Generic exceptions should never be thrown
        })
        @Nullable
        Object invoke(MethodHandle handle, @Nullable Object[] args) throws Throwable;
    }
}
