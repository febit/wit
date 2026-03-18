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
import org.febit.wit.util.ClassMap;
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
public class BeanUtils {

    private static final ClassMap<Map<String, Accessor>> CACHE = new ClassMap<>();

    @Nullable
    public static Object get(Object bean, String name) throws BeanException {
        var getter = accessor(bean.getClass(), name).getter;
        if (getter != null) {
            return getter.get(bean);
        }
        throw new BeanException("Unable to get getter for " + bean.getClass() + "#" + name);
    }

    public static void set(Object bean, String name, @Nullable Object value) throws BeanException {
        var setter = accessor(bean.getClass(), name).setter;
        if (setter != null) {
            setter.set(bean, value);
            return;
        }
        throw new BeanException("Unable to get setter for " + bean.getClass() + "#" + name);
    }

    private static Accessor accessor(Class<?> cls, String name) throws BeanException {
        var descs = CACHE.unsafeGet(cls);
        if (descs == null) {
            descs = CACHE.putIfAbsent(cls, createAccessors(cls));
        }
        var accessor = descs.get(name);
        if (accessor != null) {
            return accessor;
        }
        throw new BeanException("Unable to get field: " + cls + "#" + name);
    }

    private static Map<String, Accessor> createAccessors(Class<?> cls) {
        var map = new HashMap<String, Accessor>(16);
        BeanProperties.introspect(cls)
                .forEach(prop -> map.put(
                        prop.name(),
                        new Accessor(prop.getter(), prop.setter())
                ));
        return map;
    }

    private record Accessor(@Nullable Getter getter, @Nullable Setter setter) {
    }

    public interface Getter {
        @Nullable
        Object get(Object bean);
    }

    public interface Setter {

        Class<?> propertyType();

        void set(Object bean, @Nullable Object value);
    }

    record MethodGetter(Method method) implements Getter {
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

    record MethodSetter(Method method, Class<?> propertyType) implements Setter {
        @Override
        public void set(Object bean, @Nullable Object value) throws BeanException {
            try {
                this.method.invoke(bean, value);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                throw new BeanException(ex.toString(), ex);
            }
        }
    }

    record FieldGetter(Field field) implements Getter {
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

    record FieldSetter(Field field, Class<?> propertyType) implements Setter {
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
