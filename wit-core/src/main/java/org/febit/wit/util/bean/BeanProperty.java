// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.util.bean;

import org.febit.wit.util.ClassUtils;
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
) implements Comparable<BeanProperty> {

    public BeanUtils.@Nullable Getter getter() {
        if (getterMethod != null) {
            getterMethod.trySetAccessible();
            return new BeanUtils.MethodGetter(getterMethod);
        }
        if (field != null) {
            field.trySetAccessible();
            return new BeanUtils.FieldGetter(field);
        }
        return null;
    }

    public BeanUtils.@Nullable Setter setter() {
        if (setterMethod != null) {
            setterMethod.trySetAccessible();
            var propertyType = setterMethod.getParameterTypes()[0];
            return new BeanUtils.MethodSetter(setterMethod, propertyType);
        }
        if (field != null && !isReadonlyField()) {
            field.trySetAccessible();
            return new BeanUtils.FieldSetter(field, field.getType());
        }
        return null;
    }

    public boolean isReadonlyField() {
        return this.field != null && ClassUtils.isFinal(this.field);
    }

    @Override
    public int compareTo(final BeanProperty o) {
        return Integer.compare(name().hashCode(), o.name().hashCode());
    }

    @Override
    public int hashCode() {
        return name().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof BeanProperty other)) {
            return false;
        }
        return this.beanType() == other.beanType()
                && this.name().equals(other.name());
    }
}
