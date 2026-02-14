// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.util;

import lombok.experimental.UtilityClass;
import org.febit.wit.core.NativeFactory;
import org.febit.wit.exceptions.AmbiguousMethodException;
import org.febit.wit.exceptions.ScriptRuntimeException;
import org.febit.wit.exceptions.UncheckedException;
import org.febit.wit.runtime.Undefined;
import org.febit.wit.runtime.heap.StaticHeaps;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

@SuppressWarnings({
        "java:S135", // Loops should not contain more than a single "break" or "continue" statement
        "java:S1452", // Generic wildcard types should not be used in return types
})
@UtilityClass
public class JavaNativeUtils {

    private static final int COST_NEVER = -1;
    private static final int COST_EXACT = 0;
    private static final int COST_ASSIGNABLE = 1;
    private static final int COST_OBJECT = 100;
    private static final int COST_PRIMITIVE = 10;
    // XXX COST_CONVERT = 1000
    private static final int COST_NULL = 1000000;

    private static final Class<?>[] EMPTY_CLASSES = new Class<?>[0];

    @SuppressWarnings("UnusedReturnValue")
    public static int addStaticMethods(
            StaticHeaps heaps, NativeFactory nativeFactory, Class<?> type) {
        return addStaticMethods(heaps, nativeFactory, type, false);
    }

    public static int addStaticMethods(
            StaticHeaps heaps,
            NativeFactory nativeFactory,
            Class<?> type,
            boolean skipConflict
    ) {
        var methodMap = Arrays.stream(type.getMethods())
                .filter(ClassUtils::isStatic)
                .filter(m -> !(skipConflict && heaps.constant().has(m.getName())))
                .collect(Collectors.groupingBy(Method::getName));

        methodMap.forEach((name, methods) ->
                heaps.constant().set(name, nativeFactory.createNativeMethodDeclare(methods))
        );
        return methodMap.size();
    }

    @SuppressWarnings("UnusedReturnValue")
    public static int addConstFields(StaticHeaps heaps, Class<?> type) {
        return addConstFields(heaps, type, false);
    }

    public static int addConstFields(
            StaticHeaps heaps,
            Class<?> type,
            boolean ignoreIfConflict
    ) {
        int count = 0;
        for (var field : type.getFields()) {
            if (!ClassUtils.isStatic(field)
                    || !ClassUtils.isFinal(field)) {
                continue;
            }
            String name = field.getName();
            if (ignoreIfConflict && heaps.constant().has(name)) {
                continue;
            }
            ClassUtils.setAccessible(field);
            try {
                heaps.constant().set(name, field.get(null));
            } catch (IllegalArgumentException | IllegalAccessException e) {
                throw new UncheckedException(e);
            }
        }
        return count;
    }

    public static @Nullable Class<?>[] getArgTypes(@Nullable Object @Nullable [] args) {
        if (args == null || args.length == 0) {
            return EMPTY_CLASSES;
        }

        @Nullable
        Class<?>[] argTypes = new Class[args.length];
        for (int i = 0; i < argTypes.length; i++) {
            var arg = args[i];
            argTypes[i] = arg != null ? arg.getClass() : null;
        }
        return argTypes;
    }

    /**
     * @param methods methods
     * @param args    args
     * @return null if not found
     */
    @Nullable
    public static Method getMatchMethod(Method[] methods, @Nullable Object @Nullable [] args) {
        return getMatchMethod(methods, getArgTypes(args));
    }

    /**
     * @param methods methods
     * @param args    if mixed, first arg is the host of member methods.
     * @param mix     if mix, static methods with member methods
     * @return null if not found
     */
    @Nullable
    public static Method getMatchMethod(Method[] methods, @Nullable Object @Nullable [] args, boolean mix) {
        return getMatchMethod(methods, getArgTypes(args), mix);
    }

    /**
     * @return null if not found
     */
    @Nullable
    public static Method getMatchMethod(Method[] methods, @Nullable Class<?>[] argTypes) {
        return getMatchMethod(methods, argTypes, false);
    }

    /**
     * @param methods  methods
     * @param argTypes if mixed, first arg is the host of member methods.
     * @param mix      if mix static methods with member methods
     * @return null if not found
     */
    @SuppressWarnings({
            "squid:S3776" // Cognitive Complexity of methods should not be too high
    })
    @Nullable
    public static Method getMatchMethod(Method @Nullable [] methods, @Nullable Class<?>[] argTypes, boolean mix) {
        if (methods == null
                || methods.length == 0) {
            return null;
        }
        Method[] candidate = new Method[methods.length];
        int candidateCount = 0;
        int leastCost = Integer.MAX_VALUE;

        @Nullable Class<?>[] argTypesForMemberMethods = null;
        for (Method method : methods) {
            int cost;
            if (mix && !ClassUtils.isStatic(method)) {
                if (argTypes.length == 0
                        || argTypes[0] == null
                        || !method.getDeclaringClass().isAssignableFrom(argTypes[0])) {
                    continue;
                }
                if (argTypesForMemberMethods == null) {
                    argTypesForMemberMethods = Arrays.copyOfRange(argTypes, 1, argTypes.length);
                }
                cost = getAssignCost(argTypesForMemberMethods, method.getParameterTypes());
            } else {
                cost = getAssignCost(argTypes, method.getParameterTypes());
            }
            if (cost < 0) {
                continue;
            }
            if (cost == leastCost) {
                candidate[candidateCount++] = method;
            } else if (cost < leastCost) {
                leastCost = cost;
                candidate[0] = method;
                candidateCount = 1;
            }
        }
        if (candidateCount > 1) {
            Method method = resolveAmbiguousMethods(argTypes, candidate, candidateCount, mix);
            if (method != null) {
                return method;
            }
            throw new AmbiguousMethodException(
                    Arrays.copyOf(candidate, candidateCount),
                    argTypes);
        }
        return candidate[0];
    }

    /**
     * @param argTypes argTypes
     * @param methods  methods
     * @param count    count
     * @param mix      mix
     * @return null if can't resolve
     */
    @Nullable
    static Method resolveAmbiguousMethods(@Nullable Class<?>[] argTypes, Method[] methods, int count, boolean mix) {
        if (argTypes.length == 0) {
            return null;
        }
        Method candidate = methods[0];
        Class<?>[] candidateArgs = candidate.getParameterTypes();
        for (int i = 1; i < count; i++) {
            Method next = methods[i];
            if (mix && ClassUtils.isStatic(candidate) != ClassUtils.isStatic(next)) {
                // current not support
                return null;
            }
            Class<?>[] nextArgs = next.getParameterTypes();
            int cost = getAssignCost(nextArgs, candidateArgs);
            if (cost == 0) {
                return null;
            }
            if (cost > 0) {
                candidate = next;
                candidateArgs = nextArgs;
            } else if (getAssignCost(candidateArgs, nextArgs) <= 0) {
                // ambiguous
                return null;
            }
        }
        return candidate;
    }

    /**
     * @param constructors constructors
     * @param args         args
     * @return null if not found
     */
    @Nullable
    public static Constructor<?> getMatchConstructor(
            Constructor<?> @Nullable [] constructors, @Nullable Object @Nullable [] args) {
        return getMatchConstructor(constructors, getArgTypes(args));
    }

    /**
     * @param constructors constructors
     * @param argTypes     if mixed, first arg is the host of member methods.
     * @return null if not found
     */
    @Nullable
    public static Constructor<?> getMatchConstructor(
            Constructor<?> @Nullable [] constructors, @Nullable Class<?>[] argTypes) {
        if (constructors == null
                || constructors.length == 0) {
            return null;
        }
        var candidate = new Constructor[constructors.length];
        int candidateCount = 0;
        int leastCost = Integer.MAX_VALUE;
        for (var constructor : constructors) {
            int cost = getAssignCost(argTypes, constructor.getParameterTypes());
            if (cost < 0) {
                continue;
            }
            if (cost == leastCost) {
                candidate[candidateCount++] = constructor;
            } else if (cost < leastCost) {
                leastCost = cost;
                candidate[0] = constructor;
                candidateCount = 1;
            }
        }
        if (candidateCount > 1) {
            var constructor = resolveAmbiguousConstructors(argTypes, candidate, candidateCount);
            if (constructor != null) {
                return constructor;
            }
            throw new AmbiguousMethodException(
                    Arrays.copyOf(candidate, candidateCount),
                    argTypes);
        }
        return candidate[0];
    }

    @Nullable
    static Constructor<?> resolveAmbiguousConstructors(
            @Nullable Class<?>[] argTypes, Constructor<?> @Nullable [] constructors, int count) {
        if (constructors == null
                || constructors.length == 0) {
            return null;
        }
        if (argTypes.length == 0) {
            return null;
        }
        var candidate = constructors[0];
        Class<?>[] candidateArgs = candidate.getParameterTypes();
        for (int i = 1; i < count; i++) {
            var next = constructors[i];
            Class<?>[] nextArgs = next.getParameterTypes();
            int cost = getAssignCost(nextArgs, candidateArgs);
            if (cost == 0) {
                return null;
            }
            if (cost > 0) {
                candidate = next;
                candidateArgs = nextArgs;
            } else if (getAssignCost(candidateArgs, nextArgs) <= 0) {
                // ambiguous
                return null;
            }
        }
        return candidate;
    }

    static int getAssignCost(@Nullable Class<?>[] froms, Class<?>[] tos) {
        if (froms.length > tos.length) {
            return COST_NEVER;
        }
        int totalCost = (tos.length - froms.length) * COST_NULL;
        for (int i = 0; i < froms.length; i++) {
            int cost = getAssignCost(froms[i], tos[i]);
            if (cost < 0) {
                return -1;
            }
            totalCost += cost;
        }
        return totalCost;
    }

    static int getAssignCost(@Nullable Class<?> passedType, Class<?> acceptType) {
        if (passedType == null) {
            return acceptType.isPrimitive() ? COST_NEVER : COST_NULL;
        }
        if (passedType.equals(acceptType)) {
            return COST_EXACT;
        }
        if (acceptType.isPrimitive()) {
            return passedType == ClassUtils.getBoxedPrimitiveClass(acceptType)
                    ? COST_PRIMITIVE
                    : COST_NEVER;
        }
        if (passedType.isPrimitive()) {
            return acceptType == ClassUtils.getBoxedPrimitiveClass(passedType)
                    ? COST_PRIMITIVE
                    : COST_NEVER;
        }
        if (acceptType.isAssignableFrom(passedType)) {
            return acceptType == Object.class ? COST_OBJECT : COST_ASSIGNABLE;
        }
        //TODO: support auto convert
        return COST_NEVER;
    }

    @Nullable
    public static Object invokeMethod(Method method, @Nullable Object @Nullable [] args) {
        if (ClassUtils.isStatic(method)) {
            return invokeMethod(method, null, args);
        }
        if (args == null || args.length == 0 || args[0] == null) {
            throw new ScriptRuntimeException("this method need one argument at least");
        }
        var methodArgs = prepareArgs(method.getParameterCount(), args, 1);
        return invokeMethod(method, args[0], methodArgs);
    }

    public static @Nullable Object[] prepareArgs(
            int acceptArgsCount, @Nullable Object @Nullable [] args, final int from) {
        if (args == null) {
            return acceptArgsCount == 0
                    ? ArrayUtils.emptyObjects()
                    : new Object[acceptArgsCount];
        }
        if (from == 0 && args.length == acceptArgsCount) {
            return args;
        }
        final Object[] result = new Object[acceptArgsCount];
        System.arraycopy(args, from, result, 0, Math.min(args.length - from, acceptArgsCount));
        return result;
    }

    @Nullable
    public static Object invokeMethod(
            final Method method,
            @Nullable Object me,
            @Nullable Object @Nullable [] args
    ) {
        var methodArgs = prepareArgs(method.getParameterCount(), args, 0);
        try {
            Object result = method.invoke(me, methodArgs);
            return ClassUtils.isVoidType(method.getReturnType())
                    ? Undefined.UNDEFINED
                    : result;
        } catch (IllegalAccessException ex) {
            throw new ScriptRuntimeException("this method is inaccessible: " + ex.getLocalizedMessage(), ex);
        } catch (IllegalArgumentException ex) {
            throw new ScriptRuntimeException("illegal argument: " + ex.getLocalizedMessage(), ex);
        } catch (InvocationTargetException ex) {
            throw new ScriptRuntimeException("this method throws an exception", ex);
        }
    }

    public static Object invokeConstructor(
            Constructor<?> constructor, @Nullable Object @Nullable [] args) {
        var methodArgs = prepareArgs(constructor.getParameterCount(), args, 0);
        try {
            return constructor.newInstance(methodArgs);
        } catch (InstantiationException ex) {
            throw new ScriptRuntimeException("Can't create new instance: " + ex.getLocalizedMessage(), ex);
        } catch (IllegalAccessException ex) {
            throw new ScriptRuntimeException("Inaccessible method: " + ex.getLocalizedMessage(), ex);
        } catch (IllegalArgumentException ex) {
            throw new ScriptRuntimeException("Illegal arguments: " + ex.getLocalizedMessage(), ex);
        } catch (InvocationTargetException ex) {
            throw new ScriptRuntimeException("this method throws an exception", ex);
        }
    }

}
