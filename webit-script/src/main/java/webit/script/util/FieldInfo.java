// Copyright (c) 2013, Webit Team. All Rights Reserved.

package webit.script.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 *
 * @author Zqq
 */
public class FieldInfo implements Comparable<FieldInfo> {

    private final String name;
    private final int hashCode;
    private final Class parent;
    private Method getterMethod;
    private Field field;
    private Method setterMethod;
    private boolean isFinal = false;

    public FieldInfo(Class parent, String name) {
        this.parent = parent;
        this.name = name;
        this.hashCode = name.hashCode();
    }

    public String getName() {
        return name;
    }

    public int getHashCode() {
        return hashCode;
    }

    public Class getParent() {
        return parent;
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
        return isFinal;
    }

    public void setIsFinal(boolean isFinal) {
        this.isFinal = isFinal;
    }

    public int compareTo(FieldInfo o) {
        return this.hashCode - o.hashCode;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final FieldInfo other = (FieldInfo) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        if (this.parent != other.parent && (this.parent == null || !this.parent.equals(other.parent))) {
            return false;
        }
        return true;
    }
}
