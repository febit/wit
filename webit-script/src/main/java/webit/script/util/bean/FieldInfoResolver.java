// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.util.bean;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import webit.script.util.ClassUtil;
import webit.script.util.StringUtil;

/**
 *
 * @author Zqq
 */
public final class FieldInfoResolver {

    private final Class beanClass;
    private final Map<String, FieldInfo> fieldInfos;

    public FieldInfoResolver(Class beanClass) {
        this.beanClass = beanClass;
        this.fieldInfos = new HashMap<String, FieldInfo>();
    }

    public static FieldInfo[] resolve(Class beanClass) {
        return new FieldInfoResolver(beanClass).resolve();
    }

    public FieldInfo[] resolve() {

        for (Field field : beanClass.getFields()) {
            if (!ClassUtil.isStatic(field)) {
                registField(field);
            }
        }

        for (Method method : beanClass.getMethods()) {
            if (!ClassUtil.isStatic(method)
                    && method.getDeclaringClass() != Object.class) {
                int argsCount = method.getParameterTypes().length;
                String methodName = method.getName();
                int methodNameLength = methodName.length();
                if (method.getReturnType() == void.class) {
                    if (argsCount == 1
                            && methodNameLength > 3
                            && methodName.startsWith("set")) {
                        registSetterMethod(StringUtil.cutFieldName(methodName, 3), method);
                    }
                } else {
                    if (argsCount == 0) {
                        if (methodNameLength > 3
                                && methodName.startsWith("get")) {
                            registGetterMethod(StringUtil.cutFieldName(methodName, 3), method);
                        } else if (methodNameLength > 2
                                && methodName.startsWith("is")) {
                            registGetterMethod(StringUtil.cutFieldName(methodName, 2), method);
                        }
                    }
                }
            }
        }

        final FieldInfo[] fieldInfoArray = fieldInfos.values().toArray(new FieldInfo[fieldInfos.size()]);
        Arrays.sort(fieldInfoArray);
        return fieldInfoArray;
    }

    private FieldInfo getOrCreateFieldInfo(String name) {
        FieldInfo fieldInfo = fieldInfos.get(name);
        if (fieldInfo == null) {
            fieldInfo = new FieldInfo(beanClass, name);
            fieldInfos.put(name, fieldInfo);
        }
        return fieldInfo;
    }

    private void registField(Field field) {
        getOrCreateFieldInfo(field.getName()).setField(field);
    }

    private void registGetterMethod(String name, Method method) {
        getOrCreateFieldInfo(name).setGetterMethod(method);
    }

    private void registSetterMethod(String name, Method method) {
        getOrCreateFieldInfo(name).setSetterMethod(method);
    }
}
