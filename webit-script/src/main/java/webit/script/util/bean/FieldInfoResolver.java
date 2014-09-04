// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.util.bean;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import webit.script.util.ClassUtil;

/**
 *
 * @author Zqq
 */
public final class FieldInfoResolver {

    private final Class beanType;
    private final Map<String, FieldInfo> fieldInfos;

    private FieldInfoResolver(Class beanClass) {
        this.beanType = beanClass;
        this.fieldInfos = new HashMap<String, FieldInfo>();
    }

    public static FieldInfo[] resolve(Class beanClass) {
        return new FieldInfoResolver(beanClass).resolve();
    }

    private FieldInfo[] resolve() {

        for (Field field : beanType.getFields()) {
            if (!ClassUtil.isStatic(field)) {
                registField(field);
            }
        }

        for (Method method : beanType.getMethods()) {
            if (!ClassUtil.isStatic(method)
                    && method.getDeclaringClass() != Object.class) {
                int argsCount = method.getParameterTypes().length;
                String methodName = method.getName();
                int methodNameLength = methodName.length();
                if (method.getReturnType() == void.class) {
                    if (argsCount == 1
                            && methodNameLength > 3
                            && methodName.startsWith("set")) {
                        registSetterMethod(cutFieldName(methodName, 3), method);
                    }
                } else {
                    if (argsCount == 0) {
                        if (methodNameLength > 3
                                && methodName.startsWith("get")) {
                            registGetterMethod(cutFieldName(methodName, 3), method);
                        } else if (methodNameLength > 2
                                && methodName.startsWith("is")) {
                            registGetterMethod(cutFieldName(methodName, 2), method);
                        }
                    }
                }
            }
        }

        final FieldInfo[] fieldInfoArray = fieldInfos.values().toArray(new FieldInfo[fieldInfos.size()]);
        return fieldInfoArray;
    }

    private FieldInfo getOrCreateFieldInfo(String name) {
        FieldInfo fieldInfo = fieldInfos.get(name);
        if (fieldInfo == null) {
            fieldInfo = new FieldInfo(beanType, name);
            fieldInfos.put(name, fieldInfo);
        }
        return fieldInfo;
    }

    private void registField(Field field) {
        getOrCreateFieldInfo(field.getName()).field = field;
    }

    private void registGetterMethod(String name, Method method) {
        getOrCreateFieldInfo(name).getter = method;
    }

    private void registSetterMethod(String name, Method method) {
        getOrCreateFieldInfo(name).setter = method;
    }

    static String cutFieldName(final String string, final int from) {
        final int nextIndex = from + 1;
        final int len = string.length();
        char c;
        if (len > nextIndex) {
            c = string.charAt(nextIndex);
            if ((c >= 'A') && (c <= 'Z')) {
                return string.substring(from);
            }
        }
        char[] buffer = new char[len - from];
        string.getChars(from, len, buffer, 0);
        c = buffer[0];
        if ((c >= 'A') && (c <= 'Z')) {
            buffer[0] = (char) (c + 0x20);
        }
        return new String(buffer);
    }
}
