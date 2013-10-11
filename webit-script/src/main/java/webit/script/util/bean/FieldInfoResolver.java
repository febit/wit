// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.util.bean;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import webit.script.util.CharUtil;

/**
 *
 * @author Zqq
 */
public final class FieldInfoResolver {

    public static FieldInfo[] resolver(Class beanClass) {
        return new FieldInfoResolver(beanClass).resolver();
    }
    private final Class beanClass;

    public FieldInfoResolver(Class beanClass) {
        this.beanClass = beanClass;
    }

    public FieldInfo[] resolver() {

        Field[] fields = beanClass.getFields();

        Field field;
        int modifiers;

        for (int i = 0, len = fields.length; i < len; i++) {
            field = fields[i];
            modifiers = field.getModifiers();
            if (Modifier.isStatic(modifiers) == false) {
                registField(field);
            } else {
                continue;
            }
        }

        final Method[] methods = beanClass.getMethods();
        Method method;
        String methodName;
        int argsCount;
        for (int i = 0, len = methods.length; i < len; i++) {
            method = methods[i];
            modifiers = method.getModifiers();
            if (Modifier.isStatic(modifiers) == false) {
                argsCount = method.getParameterTypes().length;
                if (argsCount == 1 && method.getReturnType() == void.class) {
                    methodName = method.getName();
                    if (methodName.length() > 3
                            && methodName.startsWith("set")) {
                        registSetterMethod(lowerFirst(methodName.substring(3)), method);
                    }
                } else if (argsCount == 0) {
                    methodName = method.getName();
                    if (methodName.length() > 3
                            && methodName.startsWith("get")
                            && (methodName.equals("getClass") == false)) {

                        registGetterMethod(lowerFirst(methodName.substring(3)), method);
                    } else if (methodName.length() > 2
                            && methodName.startsWith("is")) {
                        registGetterMethod(lowerFirst(methodName.substring(2)), method);
                    }
                }
            }
        }

        final FieldInfo[] fieldInfoArray;
        Arrays.sort(fieldInfoArray = fieldInfos.values().toArray(new FieldInfo[fieldInfos.size()]));
        return fieldInfoArray;
    }
    private Map<String, FieldInfo> fieldInfos = new HashMap<String, FieldInfo>();

    private static String lowerFirst(String string) {
        return String.valueOf(CharUtil.toLowerAscii(string.charAt(0))).concat(string.substring(1));
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
