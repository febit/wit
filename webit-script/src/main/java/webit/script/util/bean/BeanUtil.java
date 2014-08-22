// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.util.bean;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import webit.script.util.ClassMap;
import webit.script.util.ClassUtil;
import webit.script.util.StringUtil;

/**
 *
 * @author Zqq
 */
public class BeanUtil {

    private static final ClassMap<FieldDescriptorsBox> CACHE = new ClassMap<FieldDescriptorsBox>();

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
            if (convertIfNeed && (value == null || value instanceof String)) {
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
        Map<String, FieldDescriptor> descs;
        if ((descs = box.items) == null) {
            synchronized (box) {
                if ((descs = box.items) == null) {
                    descs = resolveFieldDescriptors(cls);
                    box.items = descs;
                }
            }
        }

        FieldDescriptor fieldDescriptor;
        if ((fieldDescriptor = descs.get(name)) != null) {
            return fieldDescriptor;
        } else {
            throw new BeanUtilException(StringUtil.concat("Unable to get field: ", cls.getName(), "#", name));
        }
    }

    private static Map<String, FieldDescriptor> resolveFieldDescriptors(Class cls) {
        final FieldInfo[] fieldInfos = FieldInfoResolver.resolve(cls);
        final Map<String, FieldDescriptor> map = new HashMap<String, FieldDescriptor>(fieldInfos.length * 4 / 3 + 1, 0.75f);
        FieldInfo fieldInfo;
        for (int i = 0, len = fieldInfos.length; i < len;) {
            fieldInfo = fieldInfos[i++];
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

        Map<String, FieldDescriptor> items;

        FieldDescriptorsBox() {
        }
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

    private static final class MethodGetter implements Getter {

        private final Method method;

        MethodGetter(Method method) {
            ClassUtil.setAccessible(method);
            this.method = method;
        }

        public Object get(Object bean) throws BeanUtilException {
            try {
                return this.method.invoke(bean, (Object[]) null);
            } catch (Exception ex) {
                throw new BeanUtilException(ex.getMessage());
            }
        }
    }

    private static final class MethodSetter implements Setter {

        private final Method method;
        private final Class fieldType;

        MethodSetter(Method method) {
            ClassUtil.setAccessible(method);
            this.method = method;
            this.fieldType = method.getParameterTypes()[0];
        }

        public Class getPropertyType() {
            return this.fieldType;
        }

        public void set(Object bean, Object value) throws BeanUtilException {
            try {
                this.method.invoke(bean, new Object[]{value});
            } catch (Exception ex) {
                throw new BeanUtilException(ex.getMessage());
            }
        }
    }

    private static final class FieldGetter implements Getter {

        private final Field field;

        FieldGetter(Field field) {
            ClassUtil.setAccessible(field);
            this.field = field;
        }

        public Object get(Object bean) throws BeanUtilException {
            try {
                return this.field.get(bean);
            } catch (Exception ex) {
                throw new BeanUtilException(ex.getMessage());
            }
        }
    }

    private static final class FieldSetter implements Setter {

        private final Field field;
        private final Class fieldType;

        FieldSetter(Field field) {
            ClassUtil.setAccessible(field);
            this.field = field;
            this.fieldType = field.getType();
        }

        public void set(Object bean, Object value) throws BeanUtilException {
            try {
                this.field.set(bean, value);
            } catch (Exception ex) {
                throw new BeanUtilException(ex.getMessage());
            }
        }

        public Class getPropertyType() {
            return this.fieldType;
        }
    }
}
