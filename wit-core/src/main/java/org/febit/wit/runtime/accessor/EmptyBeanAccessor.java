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
package org.febit.wit.runtime.accessor;

import lombok.RequiredArgsConstructor;
import org.febit.wit.exception.ScriptEvaluateException;
import org.jspecify.annotations.Nullable;

@RequiredArgsConstructor
public class EmptyBeanAccessor<T> implements GenericBeanAccessor<T> {

    private static final EmptyBeanAccessor<?> INSTANCE = new EmptyBeanAccessor<>();

    @SuppressWarnings("unchecked")
    public static <T> EmptyBeanAccessor<T> get() {
        return (EmptyBeanAccessor<T>) INSTANCE;
    }

    @Nullable
    @Override
    public Object get(Object bean, @Nullable Object property) {
        if (property == null) {
            throw new ScriptEvaluateException("property should not be null for bean access.");
        }
        throw new ScriptEvaluateException("no such property: " + bean.getClass().getName() + "#" + property);
    }

    @Override
    public void set(Object bean, @Nullable Object property, @Nullable Object value) {
        get(bean, property);
    }
}
