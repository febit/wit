// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.util.bean;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 *
 * @author zqq90
 */
public final class FieldInfo implements Comparable<FieldInfo> {

    public final String name;
    public final Class owner;
    public final int hashOfName;
    Field field;
    Method getterMethod;
    Method setterMethod;

    public FieldInfo(Class owner, String name) {
        this.owner = owner;
        this.name = name;
        this.hashOfName = name.hashCode();
    }

    public Method getGetterMethod() {
        return getterMethod;
    }

    public Method getSetterMethod() {
        return setterMethod;
    }

    public BeanUtil.Getter getGetter() {
        if (getterMethod != null) {
            return new BeanUtil.MethodGetter(getterMethod);
        }
        if (field != null) {
            return new BeanUtil.FieldGetter(field);
        }
        return null;
    }

    public BeanUtil.Setter getSetter() {
        if (setterMethod != null) {
            return new BeanUtil.MethodSetter(setterMethod);
        }
        if (isFieldSettable()) {
            return new BeanUtil.FieldSetter(field);
        }
        return null;
    }

    public Field getField() {
        return field;
    }

    public boolean isFieldSettable() {
        return this.field != null && !Modifier.isFinal(this.field.getModifiers());
    }

    @Override
    public int compareTo(final FieldInfo o) {
        return (this.hashOfName < o.hashOfName) ? -1 : ((this.hashOfName == o.hashOfName) ? 0 : 1);
    }

    @Override
    public int hashCode() {
        return hashOfName;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null
                || !(obj instanceof FieldInfo)) {
            return false;
        }
        final FieldInfo other = (FieldInfo) obj;
        return this.owner == other.owner && this.name.equals(other.name);
    }
}
