// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.util.bean;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.febit.wit.util.ClassUtils;

import java.util.HashMap;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.lang.Character.isUpperCase;
import static java.lang.Character.toLowerCase;

@Slf4j
@UtilityClass
public class BeanProperties {

    public static Stream<BeanProperty> introspect(Class<?> beanType) {
        var props = new HashMap<String, BeanProperty.Builder>();
        var sink = (Function<String, BeanProperty.Builder>) name ->
                props.computeIfAbsent(name, n -> BeanProperty.builder().name(n));

        inspectRecord(beanType, sink);
        inspectPublicFields(beanType, sink);
        inspectStandardBeanMethods(beanType, sink);

        return props.values().stream()
                .map(b -> b.beanType(beanType).build());
    }

    static String resolveNameFromMethod(final String raw, final int offset) {
        var len = raw.length();
        if (len > offset + 1
                && isUpperCase(raw.charAt(offset + 1))) {
            return raw.substring(offset);
        }

        var buff = new char[len - offset];
        raw.getChars(offset, len, buff, 0);

        if (isUpperCase(buff[0])) {
            buff[0] = toLowerCase(buff[0]);
        }
        return new String(buff);
    }

    private static void inspectRecord(Class<?> beanType, Function<String, BeanProperty.Builder> collector) {
        if (!beanType.isRecord()) {
            return;
        }
        var components = beanType.getRecordComponents();
        if (components == null) {
            return;
        }
        for (var comp : components) {
            var name = comp.getName();
            var accessor = comp.getAccessor();
            collector.apply(name).getterMethod(accessor);
        }
    }

    private static void inspectPublicFields(Class<?> beanType, Function<String, BeanProperty.Builder> collector) {
        for (var field : beanType.getFields()) {
            if (!ClassUtils.isStatic(field)) {
                collector.apply(field.getName()).field(field);
            }
        }
    }

    private static void inspectStandardBeanMethods(Class<?> beanType, Function<String, BeanProperty.Builder> collector) {
        for (var method : beanType.getMethods()) {
            if (ClassUtils.isStatic(method)
                    || method.getDeclaringClass() == Object.class) {
                continue;
            }
            var argsCount = method.getParameterCount();
            var methodName = method.getName();
            var methodNameLength = methodName.length();
            if (argsCount == 0 && method.getReturnType() != void.class) {
                if (methodNameLength > 3 && methodName.startsWith("get")) {
                    var name = resolveNameFromMethod(methodName, 3);
                    collector.apply(name).getterMethod(method);
                } else if (methodNameLength > 2 && methodName.startsWith("is")
                        && method.getReturnType() == boolean.class) {
                    var name = resolveNameFromMethod(methodName, 2);
                    collector.apply(name).getterMethod(method);
                }
            } else if (argsCount == 1
                    && methodNameLength > 3
                    && method.getReturnType() == void.class
                    && methodName.startsWith("set")) {
                var name = resolveNameFromMethod(methodName, 3);
                collector.apply(name).setterMethod(method);
            }
        }
    }
}
