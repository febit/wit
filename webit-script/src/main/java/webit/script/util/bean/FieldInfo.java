// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.util.bean;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 *
 * @author Zqq
 */
public final class FieldInfo implements Comparable<FieldInfo> {

    public final String name;
    public final int hashCode;
    public final Class parent;
    private Field field;
    private Method getterMethod;
    private Method setterMethod;

    public FieldInfo(Class parent, String name) {
        this.parent = parent;
        this.name = name;
        this.hashCode = name.hashCode();
    }

    public Method getGetterMethod() {
        return getterMethod;
    }

    public void setGetterMethod(Method getterMethod) {
        this.getterMethod = getterMethod;
    }

    public Method getSetterMethod() {
        return setterMethod;
    }

    public void setSetterMethod(Method setterMethod) {
        this.setterMethod = setterMethod;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public boolean isIsFinal() {
        return this.field != null && Modifier.isFinal(field.getModifiers());
    }

    public int compareTo(final FieldInfo o) {
        return (this.hashCode < o.hashCode) ? -1 : ((this.hashCode == o.hashCode) ? 0 : 1);
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null
                ||!(obj instanceof FieldInfo)) {
            return false;
        }
        final FieldInfo other = (FieldInfo) obj;
        return this.parent == other.parent && this.name.equals(other.name);
    }
}
