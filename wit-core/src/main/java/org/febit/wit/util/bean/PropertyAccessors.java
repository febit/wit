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
import org.febit.wit.util.MethodHandleUtils;
import org.jspecify.annotations.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class PropertyAccessors {

    private static final MethodType MT_GETTER = MethodType.methodType(Object.class, Object.class);
    private static final MethodType MT_SETTER = MethodType.methodType(void.class, Object.class, Object.class);

    public static Map<String, PropertyAccessor> of(Class<?> cls) {
        var map = new HashMap<String, PropertyAccessor>(16);
        BeanProperties.introspect(cls)
                .forEach(prop -> map.put(
                        prop.name(),
                        new PropertyAccessor(prop.getter(), prop.setter())
                ));
        return Map.copyOf(map);
    }

    static PropertyAccessor.Getter getterOf(Method method) {
        try {
            var handle = MethodHandleUtils.lookupOf(method.getDeclaringClass())
                    .unreflect(method)
                    .asType(MT_GETTER);
            return new MethodGetter(handle);
        } catch (IllegalAccessException ex) {
            throw new BeanException(ex.getMessage(), ex);
        }
    }

    static PropertyAccessor.Getter getterOf(Field field) {
        try {
            var handle = MethodHandleUtils.lookupOf(field.getDeclaringClass())
                    .unreflectVarHandle(field);
            return new FieldGetter(handle);
        } catch (IllegalAccessException ex) {
            throw new BeanException(ex.getMessage(), ex);
        }
    }

    static PropertyAccessor.Setter setterOf(Method method) {
        try {
            var handle = MethodHandleUtils.lookupOf(method.getDeclaringClass())
                    .unreflect(method)
                    .asType(MT_SETTER);
            return new MethodSetter(handle, method.getParameterTypes()[0]);
        } catch (IllegalAccessException ex) {
            throw new BeanException(ex.getMessage(), ex);
        }
    }

    static PropertyAccessor.Setter setterOf(Field field) {
        try {
            var handle = MethodHandleUtils.lookupOf(field.getDeclaringClass())
                    .unreflectVarHandle(field);
            return new FieldSetter(handle, field.getType());
        } catch (IllegalAccessException ex) {
            throw new BeanException(ex.getMessage(), ex);
        }
    }

    record MethodGetter(MethodHandle handle) implements PropertyAccessor.Getter {
        @Override
        @Nullable
        public Object get(Object bean) throws BeanException {
            try {
                return this.handle.invokeExact(bean);
            } catch (Throwable ex) {
                throw new BeanException("Cannot get property value: " + ex.getMessage(), ex);
            }
        }
    }

    record MethodSetter(MethodHandle handle, Class<?> propertyType) implements PropertyAccessor.Setter {
        @Override
        public void set(Object bean, @Nullable Object value) throws BeanException {
            try {
                this.handle.invokeExact(bean, value);
            } catch (Throwable ex) {
                throw new BeanException("Cannot set property value: " + ex.getMessage(), ex);
            }
        }
    }

    record FieldGetter(VarHandle handle) implements PropertyAccessor.Getter {
        @Nullable
        @Override
        public Object get(Object bean) {
            return this.handle.get(bean);
        }
    }

    record FieldSetter(VarHandle handle, Class<?> propertyType) implements PropertyAccessor.Setter {
        @Override
        public void set(Object bean, @Nullable Object value) {
            this.handle.set(bean, value);
        }
    }
}
