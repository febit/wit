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
package org.febit.wit.parser.support;

import org.febit.wit.parser.NativeFunctionFactory;
import org.febit.wit.runtime.WitFunction;
import org.febit.wit.runtime.function.ConstructorNativeFunction;
import org.febit.wit.runtime.function.MethodNativeFunction;
import org.febit.wit.runtime.function.MultiConstructorNativeFunction;
import org.febit.wit.runtime.function.MultiMethodMixedNativeFunction;
import org.febit.wit.runtime.function.MultiMethodNativeFunction;
import org.febit.wit.runtime.function.NewArrayNativeFunction;
import org.febit.wit.util.Modifiers;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

public class ReflectNativeFunctionFactory implements NativeFunctionFactory {

    public static final ReflectNativeFunctionFactory INSTANCE = new ReflectNativeFunctionFactory();

    @Override
    public WitFunction array(Class<?> componentType) {
        return new NewArrayNativeFunction(componentType);
    }

    @Override
    public WitFunction method(Method method) {
        method.trySetAccessible();
        return new MethodNativeFunction(method);
    }

    @Override
    public WitFunction method(List<Method> methods) {
        if (methods.isEmpty()) {
            throw new IllegalArgumentException("methods is empty");
        }
        int size = methods.size();
        if (size == 1) {
            return method(methods.get(0));
        }
        methods.forEach(Method::trySetAccessible);

        var isStatic = Modifiers.isStatic(methods.get(0));
        boolean mix = false;
        for (int i = 1; i < methods.size(); i++) {
            if (isStatic != Modifiers.isStatic(methods.get(i))) {
                mix = true;
                break;
            }
        }
        return mix ? new MultiMethodMixedNativeFunction(List.copyOf(methods))
                : new MultiMethodNativeFunction(List.copyOf(methods), isStatic);
    }

    @Override
    public WitFunction constructor(Constructor<?> constructor) {
        constructor.trySetAccessible();
        return new ConstructorNativeFunction(constructor);
    }

    @Override
    public WitFunction constructor(List<Constructor<?>> constructors) {
        if (constructors.isEmpty()) {
            throw new IllegalArgumentException("constructors is empty");
        }
        if (constructors.size() == 1) {
            return constructor(constructors.get(0));
        }
        constructors.forEach(Constructor::trySetAccessible);
        return new MultiConstructorNativeFunction(List.copyOf(constructors));
    }
}
