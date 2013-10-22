// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.util.bean;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import webit.script.util.StringUtil;
import webit.script.util.collection.ClassIdentityHashMap;

/**
 *
 * @author Zqq
 */
public class BeanUtil {

    private final static ClassIdentityHashMap<FieldDescriptorsBox> CACHE = new ClassIdentityHashMap<FieldDescriptorsBox>();

    public static Object get(final Object bean, final String name) throws BeanUtilException {

        Getter getter;
        if ((getter = getFieldDescriptor(bean.getClass(), name).getter) != null) {
            return getter.get(bean);
        } else {
            throw new BeanUtilException(StringUtil.concat("Unable to get getter for ", bean.getClass().getName(), "#", name));
        }
    }

    public static void set(final Object bean, final String name, Object value) throws BeanUtilException {
        set(bean, name, value, false);
    }

    public static void set(final Object bean, final String name, Object value, boolean convertIfNeed) throws BeanUtilException {

        Setter setter;
        if ((setter = getFieldDescriptor(bean.getClass(), name).setter) != null) {
            if (convertIfNeed && value != null && value instanceof String) {
                value = Convert.convert((String) value, setter.getPropertyType());
            }
            setter.set(bean, value);
        } else {
            throw new BeanUtilException(StringUtil.concat("Unable to get setter for ", bean.getClass().getName(), "#", name));
        }
    }

    private static FieldDescriptor getFieldDescriptor(final Class cls, final String name) throws BeanUtilException {
        FieldDescriptorsBox box;
        if ((box = CACHE.unsafeGet(cls)) == null) {
            box = CACHE.putIfAbsent(cls, new FieldDescriptorsBox());
        }
        Map<String, FieldDescriptor> descriptors;
        if ((descriptors = box.descriptors) == null) {
            synchronized (box) {
                if ((descriptors = box.descriptors) == null) {
                    descriptors = resolveClassDescriptor(cls);
                    box.descriptors = descriptors;
                }
            }
        }

        FieldDescriptor fieldDescriptor;
        if ((fieldDescriptor = descriptors.get(name)) != null) {
            return fieldDescriptor;
        } else {
            throw new BeanUtilException(StringUtil.concat("Unable to get field: ", cls.getName(), "#", name));
        }
    }

    private static Map<String, FieldDescriptor> resolveClassDescriptor(Class cls) {
        FieldInfo[] fieldInfos = FieldInfoResolver.resolver(cls);
        Map<String, FieldDescriptor> map = new HashMap<String, FieldDescriptor>(fieldInfos.length * 4 / 3 + 1, 0.75f);

        for (int i = 0, len = fieldInfos.length; i < len;) {
            FieldInfo fieldInfo = fieldInfos[i++];
            //Getter
            final Getter getter;
            if (fieldInfo.getGetterMethod() != null) {
                getter = new MethodGetter(fieldInfo.getGetterMethod());
            } else if (fieldInfo.getField() != null) {
                getter = new FieldGetter(fieldInfo.getField());
            } else {
                getter = null;
            }

            final Setter setter;
            if (fieldInfo.getSetterMethod() != null) {
                setter = new MethodSetter(fieldInfo.getSetterMethod());
            } else if (fieldInfo.getField() != null && fieldInfo.isIsFinal() == false) {
                setter = new FieldSetter(fieldInfo.getField());
            } else {
                setter = null;
            }

            map.put(fieldInfo.name, new FieldDescriptor(getter, setter));
        }
        return map;
    }

    private static final class FieldDescriptorsBox {

        Map<String, FieldDescriptor> descriptors;
    }

    private static final class FieldDescriptor {

        final Getter getter;
        final Setter setter;

        FieldDescriptor(Getter getter, Setter setter) {
            this.getter = getter;
            this.setter = setter;
        }
    }

    private static interface Getter {

        Object get(Object bean);
    }

    private static interface Setter {

        Class getPropertyType();

        void set(Object bean, Object value);
    }

    private static class MethodGetter implements Getter {

        private final Method method;

        MethodGetter(Method method) {
            setAccessible(method);
            this.method = method;
        }

        public Object get(Object bean) throws BeanUtilException {
            try {
                return method.invoke(bean, new Object[0]);
            } catch (Exception ex) {
                throw new BeanUtilException(ex.getMessage());
            }
        }
    }

    private static class MethodSetter implements Setter {

        private final Method method;
        private final Class fieldType;

        MethodSetter(Method method) {
            setAccessible(method);
            this.method = method;
            this.fieldType = method.getParameterTypes()[0];
        }

        public Class getPropertyType() {
            return fieldType;
        }

        public void set(Object bean, Object value) throws BeanUtilException {
            try {
                method.invoke(bean, new Object[]{value});
            } catch (Exception ex) {
                throw new BeanUtilException(ex.getMessage());
            }
        }
    }

    private static class FieldGetter implements Getter {

        private final Field field;

        FieldGetter(Field field) {
            setAccessible(field);
            this.field = field;
        }

        public Object get(Object bean) throws BeanUtilException {
            try {
                return field.get(bean);
            } catch (Exception ex) {
                throw new BeanUtilException(ex.getMessage());
            }
        }

        public void set(Object bean, Object value) throws BeanUtilException {
            try {
                field.set(bean, value);
            } catch (Exception ex) {
                throw new BeanUtilException(ex.getMessage());
            }
        }

        public Class getType() {
            return field.getDeclaringClass();
        }
    }

    private static class FieldSetter implements Setter {

        private final Field field;

        FieldSetter(Field field) {
            setAccessible(field);
            this.field = field;
        }

        public void set(Object bean, Object value) throws BeanUtilException {
            try {
                field.set(bean, value);
            } catch (Exception ex) {
                throw new BeanUtilException(ex.getMessage());
            }
        }

        public Class getPropertyType() {
            return field.getDeclaringClass();
        }
    }

    //
    private static void setAccessible(Field field) {
        try {
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
        } catch (SecurityException ignore) {
        }
    }
    //

    private static void setAccessible(Method method) {
        try {
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }
        } catch (SecurityException ignore) {
        }
    }
}
