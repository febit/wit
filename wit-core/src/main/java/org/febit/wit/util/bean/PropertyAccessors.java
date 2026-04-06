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
package org.febit.wit.util.bean;

import lombok.experimental.UtilityClass;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({
        "squid:RedundantThrowsDeclarationCheck"
})
@UtilityClass
public class PropertyAccessors {

    public static Map<String, PropertyAccessor> of(Class<?> cls) {
        var map = new HashMap<String, PropertyAccessor>(16);
        BeanProperties.introspect(cls)
                .forEach(prop -> map.put(
                        prop.name(),
                        new PropertyAccessor(prop.getter(), prop.setter())
                ));
        return Map.copyOf(map);
    }

    record MethodGetter(Method method) implements PropertyAccessor.Getter {
        @Override
        @Nullable
        public Object get(Object bean) throws BeanException {
            try {
                return this.method.invoke(bean, (Object[]) null);
            } catch (IllegalAccessException
                     | IllegalArgumentException
                     | InvocationTargetException ex) {
                throw new BeanException(ex.toString(), ex);
            }
        }
    }

    record MethodSetter(Method method, Class<?> propertyType) implements PropertyAccessor.Setter {
        @Override
        public void set(Object bean, @Nullable Object value) throws BeanException {
            try {
                this.method.invoke(bean, value);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                throw new BeanException(ex.toString(), ex);
            }
        }
    }

    record FieldGetter(Field field) implements PropertyAccessor.Getter {
        @Nullable
        @Override
        public Object get(Object bean) throws BeanException {
            try {
                return this.field.get(bean);
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                throw new BeanException(ex.toString(), ex);
            }
        }
    }

    record FieldSetter(Field field, Class<?> propertyType) implements PropertyAccessor.Setter {
        @Override
        public void set(Object bean, @Nullable Object value) throws BeanException {
            try {
                this.field.set(bean, value);
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                throw new BeanException(ex.toString(), ex);
            }
        }
    }
}
