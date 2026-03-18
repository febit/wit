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
package org.febit.wit.parser;

import org.febit.wit.runtime.WitFunction;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

public interface NativeFunctionFactory {

    WitFunction array(Class<?> componentType);

    WitFunction method(Method method);

    WitFunction method(List<Method> methods);

    WitFunction constructor(Constructor<?> constructor);

    WitFunction constructor(List<Constructor<?>> constructors);

    default NativeFunctionFactory withCache() {
        return CachingNativeFunctionFactory.of(this);
    }

    interface Decorator extends NativeFunctionFactory {

        NativeFunctionFactory delegate();

        @Override
        default WitFunction array(Class<?> componentType) {
            return delegate().array(componentType);
        }

        @Override
        default WitFunction method(Method method) {
            return delegate().method(method);
        }

        @Override
        default WitFunction method(List<Method> methods) {
            return delegate().method(methods);
        }

        @Override
        default WitFunction constructor(Constructor<?> constructor) {
            return delegate().constructor(constructor);
        }

        @Override
        default WitFunction constructor(List<Constructor<?>> constructors) {
            return delegate().constructor(constructors);
        }
    }
}
