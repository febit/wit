// Copyright (c) 2013, Webit Team. All Rights Reserved.
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

    public static FieldInfo[] resolver(Class beanClass) {
        return new FieldInfoResolver(beanClass).resolver();
    }
    private final Class beanClass;
    private final Map<String, FieldInfo> fieldInfos;

    public FieldInfoResolver(Class beanClass) {
        this.fieldInfos = new HashMap<String, FieldInfo>();
        this.beanClass = beanClass;
    }

    public FieldInfo[] resolver() {
        int i, len;

        Field[] fields = beanClass.getFields();
        Field field;
        for (i = 0, len = fields.length; i < len;) {
            if (ClassUtil.isStatic(field = fields[i++]) == false) {
                registField(field);
            }
        }

        final Method[] methods = beanClass.getMethods();
        Method method;
        String methodName;
        int argsCount;
        int methodNameLength;
        for (i = 0, len = methods.length; i < len;) {
            if (ClassUtil.isStatic(method = methods[i++]) == false
                    && method.getDeclaringClass() != Object.class) {
                argsCount = method.getParameterTypes().length;
                methodName = method.getName();
                methodNameLength = methodName.length();
                if (method.getReturnType() == void.class) {
                    if (argsCount == 1
                            && methodNameLength > 3
                            && methodName.startsWith("set")) {
                        registSetterMethod(StringUtil.cutAndLowerFirst(methodName, 3), method);
                    }
                } else {
                    if (argsCount == 0) {
                        if (methodNameLength > 3
                                && methodName.startsWith("get")) {
                            registGetterMethod(StringUtil.cutAndLowerFirst(methodName, 3), method);
                        } else if (methodNameLength > 2
                                && methodName.startsWith("is")) {
                            registGetterMethod(StringUtil.cutAndLowerFirst(methodName, 2), method);
                        }
                    }
                }
            }
        }

        final FieldInfo[] fieldInfoArray;
        Arrays.sort(fieldInfoArray = fieldInfos.values().toArray(new FieldInfo[fieldInfos.size()]));
        return fieldInfoArray;
    }

    private FieldInfo getOrCreateFieldInfo(String name) {
        FieldInfo fieldInfo;
        if ((fieldInfo = fieldInfos.get(name)) == null) {
            fieldInfos.put(name, fieldInfo = new FieldInfo(beanClass, name));
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
