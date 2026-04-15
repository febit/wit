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

import org.febit.wit.util.Modifiers;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

@lombok.Builder(
        builderClassName = "Builder"
)
public record BeanProperty(
        String name,
        Class<?> beanType,
        @Nullable Field field,
        @Nullable Method getterMethod,
        @Nullable Method setterMethod
) {

    public PropertyAccessor.@Nullable Getter getter() {
        if (getterMethod != null) {
            return PropertyAccessors.getterOf(getterMethod);
        }
        if (field != null) {
            return PropertyAccessors.getterOf(field);
        }
        return null;
    }

    public PropertyAccessor.@Nullable Setter setter() {
        if (setterMethod != null) {
            return PropertyAccessors.setterOf(setterMethod);
        }
        if (field != null && !isReadonlyField()) {
            return PropertyAccessors.setterOf(field);
        }
        return null;
    }

    public boolean isReadonlyField() {
        return this.field != null && Modifiers.isFinal(this.field);
    }
}
