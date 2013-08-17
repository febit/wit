// Copyright (c) 2013, Webit Team. All Rights Reserved.

package webit.script.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import jodd.util.CharUtil;

/**
 *
 * @author Zqq
 */
public class FieldInfoResolver {

    protected final Class beanClass;

    public FieldInfoResolver(Class beanClass) {
        this.beanClass = beanClass;
    }

    public FieldInfo[] resolver() {

        Field[] fields = beanClass.getFields();

        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            int modifiers = field.getModifiers();
            if (Modifier.isStatic(modifiers) == false) {
                registField(field);
            } else {
                continue;
            }
        }

        Method[] methods = beanClass.getMethods();

        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            int modifiers = method.getModifiers();
            if (Modifier.isStatic(modifiers) == false) {
                int argsCount = method.getParameterTypes().length;
                if (argsCount == 1 && method.getReturnType().equals(void.class)) {
                    //may setter

                    String methodName = method.getName();
                    if (methodName.startsWith("set")
                            && methodName.length() > 3) {

                        registSetterMethod(lowerFirst(methodName.substring(3)), method);
                    }

                } else if (argsCount == 0) {
                    //may getter
                    String methodName = method.getName();
                    if (methodName.startsWith("get")
                            && methodName.length() > 3
                            && (methodName.equals("getClass") == false)
                            && (methodName.equals("getAnnotations") == false)) {

                        registGetterMethod(lowerFirst(methodName.substring(3)), method);
                    } else if (methodName.startsWith("is")
                            && methodName.length() > 2) {

                        registGetterMethod(lowerFirst(methodName.substring(2)), method);
                    }
                }
            }
        }


        FieldInfo[] fieldInfoArray = new FieldInfo[fieldInfos.size()];
        fieldInfos.values().toArray(fieldInfoArray);

        Arrays.sort(fieldInfoArray);
        return fieldInfoArray;
    }
    protected Map<String, FieldInfo> fieldInfos = new HashMap<String, FieldInfo>();

    protected static String lowerFirst(String string) {
        return CharUtil.toLowerAscii(string.charAt(0)) + string.substring(1);
    }

    protected FieldInfo createFieldInfo(String name) {
        return new FieldInfo(beanClass, name);
    }

    protected final FieldInfo getOrCreateFieldInfo(String name) {
        FieldInfo fieldInfo = fieldInfos.get(name);
        if (fieldInfo == null) {
            fieldInfo = createFieldInfo(name);
            fieldInfos.put(name, fieldInfo);
        }
        return fieldInfo;
    }

    protected FieldInfo registField(Field field) {
        return registField(field.getName(), field);
    }

    protected FieldInfo registField(String name, Field field) {
        FieldInfo fieldInfo = getOrCreateFieldInfo(name);
        fieldInfo.setField(field);

        int modifiers = field.getModifiers();

        fieldInfo.setIsFinal(Modifier.isFinal(modifiers));

        return fieldInfo;
    }

    protected FieldInfo registGetterMethod(String name, Method method) {
        FieldInfo fieldInfo = getOrCreateFieldInfo(name);
        fieldInfo.setGetterMethod(method);
        return fieldInfo;
    }

    protected FieldInfo registSetterMethod(String name, Method method) {
        FieldInfo fieldInfo = getOrCreateFieldInfo(name);
        fieldInfo.setSetterMethod(method);
        return fieldInfo;
    }
}
