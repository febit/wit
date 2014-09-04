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
    public final Class owner;
    Field field;
    Method getter;
    Method setter;

    public FieldInfo(Class owner, String name) {
        this.owner = owner;
        this.name = name;
        this.hashCode = name.hashCode();
    }

    public Method getGetter() {
        return getter;
    }

    public Method getSetter() {
        return setter;
    }

    public Field getField() {
        return field;
    }

    public boolean isFieldSettable() {
        return this.field != null && !Modifier.isFinal(this.field.getModifiers());
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
                || !(obj instanceof FieldInfo)) {
            return false;
        }
        final FieldInfo other = (FieldInfo) obj;
        return this.owner == other.owner && this.name.equals(other.name);
    }
}
