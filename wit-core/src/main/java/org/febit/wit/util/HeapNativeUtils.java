package org.febit.wit.util;

import lombok.experimental.UtilityClass;
import org.febit.wit.exception.UncheckedException;
import org.febit.wit.parser.NativeFunctionFactory;
import org.febit.wit.runtime.heap.Heap;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

@UtilityClass
public class HeapNativeUtils {

    @SuppressWarnings("UnusedReturnValue")
    public static int addStaticMethods(
            Heap target, NativeFunctionFactory functionFactory, Class<?> type) {
        return addStaticMethods(target, functionFactory, type, false);
    }

    public static int addStaticMethods(
            Heap target,
            NativeFunctionFactory functionFactory,
            Class<?> type,
            boolean ignoreIfPresent
    ) {
        var methodMap = Arrays.stream(type.getMethods())
                .filter(ClassUtils::isStatic)
                .filter(m -> !(ignoreIfPresent && target.has(m.getName())))
                .collect(Collectors.groupingBy(Method::getName));

        methodMap.forEach((name, methods) ->
                target.set(name, functionFactory.method(methods))
        );
        return methodMap.size();
    }

    @SuppressWarnings("UnusedReturnValue")
    public static int addConstFields(Heap target, Class<?> type) {
        return addConstFields(target, type, false);
    }

    public static int addConstFields(
            Heap target,
            Class<?> type,
            boolean ignoreIfPresent
    ) {
        var fields = Arrays.stream(type.getFields())
                .filter(ClassUtils::isStatic)
                .filter(ClassUtils::isFinal)
                .filter(f -> !(ignoreIfPresent && target.has(f.getName())))
                .toList();

        fields.forEach(f -> {
            f.trySetAccessible();
            try {
                target.set(f.getName(), f.get(null));
            } catch (IllegalArgumentException | IllegalAccessException e) {
                throw new UncheckedException(e);
            }
        });
        return fields.size();
    }
}
