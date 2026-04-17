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
package org.febit.wit.engine.nativex;

import org.febit.wit.engine.WitFunction;
import org.febit.wit.engine.nativex.function.MultiMethodInvokerFunction;
import org.febit.wit.engine.nativex.function.MultiMixedMethodInvokerFunction;
import org.febit.wit.engine.nativex.function.NewArrayNativeFunction;
import org.febit.wit.engine.nativex.support.MethodInvoker;
import org.febit.wit.engine.nativex.support.MethodInvokerUtils;
import org.febit.wit.util.Modifiers;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ReflectNativeFunctionFactory implements NativeFunctionFactory {

    public static final ReflectNativeFunctionFactory INSTANCE = new ReflectNativeFunctionFactory();

    @Override
    public WitFunction.Constable array(Class<?> componentType) {
        return new NewArrayNativeFunction(componentType);
    }

    @Override
    public WitFunction.Constable method(Method method) {
        return MethodInvokerUtils.of(method);
    }

    @Override
    public WitFunction.Constable method(List<Method> methods) {
        if (methods.isEmpty()) {
            throw new IllegalArgumentException("methods is empty");
        }
        int size = methods.size();
        if (size == 1) {
            return method(methods.get(0));
        }

        List<MethodInvoker<?>> invokers = new ArrayList<>(size);
        var asStatic = Modifiers.isStatic(methods.get(0));
        var mix = false;
        for (Method method : methods) {
            var invoker = MethodInvokerUtils.of(method);
            invokers.add(invoker);
            if (asStatic != invoker.isStatic()) {
                mix = true;
            }
        }
        invokers = List.copyOf(invokers);
        return mix ? new MultiMixedMethodInvokerFunction(invokers)
                : new MultiMethodInvokerFunction(invokers, asStatic);
    }

    @Override
    public WitFunction.Constable constructor(Constructor<?> constructor) {
        return MethodInvokerUtils.of(constructor);
    }

    @Override
    public WitFunction.Constable constructor(List<Constructor<?>> constructors) {
        if (constructors.isEmpty()) {
            throw new IllegalArgumentException("constructors is empty");
        }
        if (constructors.size() == 1) {
            return constructor(constructors.get(0));
        }
        var invokers = constructors.stream()
                .map(MethodInvokerUtils::of)
                .toList();
        return new MultiMethodInvokerFunction(List.copyOf(invokers), true);
    }
}
