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

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.febit.wit.engine.accessor.Getter;
import org.febit.wit.engine.accessor.Setter;
import org.febit.wit.exception.ScriptEvaluateException;
import org.febit.wit.util.bean.BeanException;
import org.febit.wit.util.bean.PropertyAccessor;
import org.febit.wit.util.bean.PropertyAccessors;
import org.jspecify.annotations.Nullable;

import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ReflectBeanAccessor<T> implements Getter<T>, Setter<T> {

    private final Class<T> beanClass;
    private final Map<String, PropertyAccessor> accessors;

    public static <T> ReflectBeanAccessor<T> of(Class<T> beanClass) {
        var accessors = PropertyAccessors.of(beanClass);
        return new ReflectBeanAccessor<>(beanClass, accessors);
    }

    private PropertyAccessor accessor(String name) throws BeanException {
        var accessor = accessors.get(name);
        if (accessor != null) {
            return accessor;
        }
        throw new BeanException("no such property: " + beanClass + "#" + name);
    }

    @Nullable
    @Override
    public Object get(Object bean, @Nullable Object property) {
        if (property == null) {
            throw new ScriptEvaluateException("property should not be null for bean access.");
        }
        var name = property.toString();
        var getter = accessor(name).getter();
        if (getter != null) {
            return getter.get(bean);
        }
        throw new ScriptEvaluateException("property is not readable: " + bean.getClass() + "#" + name);
    }

    @Override
    public void set(Object bean, @Nullable Object property, @Nullable Object value) {
        if (property == null) {
            throw new ScriptEvaluateException("property should not be null for bean access.");
        }
        var name = property.toString();
        var setter = accessor(name).setter();
        if (setter != null) {
            setter.set(bean, value);
            return;
        }
        throw new BeanException("property is not writable: " + bean.getClass() + "#" + name);
    }

}
