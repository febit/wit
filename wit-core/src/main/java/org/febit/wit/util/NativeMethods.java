// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.util;

import lombok.experimental.UtilityClass;
import org.febit.wit.exception.AmbiguousMethodException;
import org.febit.wit.exception.ScriptEvaluateException;
import org.febit.wit.runtime.Undefined;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings({
        "java:S135", // Loops should not contain more than a single "break" or "continue" statement
        "java:S1452", // Generic wildcard types should not be used in return types
})
@UtilityClass
public class NativeMethods {

    private static final int COST_NEVER = -1;
    private static final int COST_EXACT = 0;
    private static final int COST_ASSIGNABLE = 1;
    private static final int COST_OBJECT = 100;
    private static final int COST_PRIMITIVE = 10;
    // XXX COST_CONVERT = 1000
    private static final int COST_NULL = 1000000;

    private static final Class<?>[] EMPTY_CLASSES = new Class<?>[0];

    @Nullable
    public static Object invoke(
            final Method method, @Nullable Object self, @Nullable Object @Nullable [] args
    ) {
        var methodArgs = fitArgs(args, method.getParameterCount(), 0);
        try {
            Object result = method.invoke(self, methodArgs);
            return ClassUtils.isVoidType(method.getReturnType())
                    ? Undefined.UNDEFINED
                    : result;
        } catch (IllegalAccessException ex) {
            throw new ScriptEvaluateException("this method is inaccessible: " + ex.getLocalizedMessage(), ex);
        } catch (IllegalArgumentException ex) {
            throw new ScriptEvaluateException("illegal argument: " + ex.getLocalizedMessage(), ex);
        } catch (InvocationTargetException ex) {
            throw new ScriptEvaluateException("this method throws an exception", ex);
        }
    }

    public static Object invoke(
            Constructor<?> constructor, @Nullable Object @Nullable [] args
    ) {
        var methodArgs = fitArgs(args, constructor.getParameterCount(), 0);
        try {
            return constructor.newInstance(methodArgs);
        } catch (InstantiationException ex) {
            throw new ScriptEvaluateException("Can't create new instance: " + ex.getLocalizedMessage(), ex);
        } catch (IllegalAccessException ex) {
            throw new ScriptEvaluateException("Inaccessible method: " + ex.getLocalizedMessage(), ex);
        } catch (IllegalArgumentException ex) {
            throw new ScriptEvaluateException("Illegal arguments: " + ex.getLocalizedMessage(), ex);
        } catch (InvocationTargetException ex) {
            throw new ScriptEvaluateException("this method throws an exception", ex);
        }
    }

    private static @Nullable Class<?>[] getArgTypes(@Nullable Object @Nullable [] args) {
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

    private static @Nullable Object[] fitArgs(
            @Nullable Object @Nullable [] args, int expectedSize, final int from) {
        if (args == null) {
            return expectedSize == 0
                    ? Args.empty()
                    : new Object[expectedSize];
        }
        if (from == 0 && args.length == expectedSize) {
            return args;
        }
        var fit = new Object[expectedSize];
        System.arraycopy(args, from, fit, 0, Math.min(args.length - from, expectedSize));
        return fit;
    }

    /**
     * @param methods methods
     * @param args    args
     * @return null if not found
     */
    @Nullable
    public static Method chooseMethod(List<Method> methods, @Nullable Object @Nullable [] args) {
        return chooseMethod(methods, getArgTypes(args));
    }

    /**
     * @param methods methods
     * @param args    if mixed, first arg is the host of member methods.
     * @param mix     if mix, static methods with member methods
     * @return null if not found
     */
    @Nullable
    public static Method chooseMethod(List<Method> methods, @Nullable Object @Nullable [] args, boolean mix) {
        return chooseMethod(methods, getArgTypes(args), mix);
    }

    /**
     * @return null if not found
     */
    @Nullable
    public static Method chooseMethod(List<Method> methods, @Nullable Class<?>[] argTypes) {
        return chooseMethod(methods, argTypes, false);
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
    public static Method chooseMethod(List<Method> methods, @Nullable Class<?>[] argTypes, boolean mix) {
        if (methods.isEmpty()) {
            return null;
        }
        Method[] candidate = new Method[methods.size()];
        int candidateCount = 0;
        int leastCost = Integer.MAX_VALUE;

        @Nullable Class<?>[] argTypesForMemberMethods = null;
        for (var method : methods) {
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
            Method method = tryResolveAmbiguous(candidate, argTypes, candidateCount, mix);
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
     * @param methods  methods
     * @param argTypes argTypes
     * @param count    count
     * @param mix      mix
     * @return null if can't resolve
     */
    @Nullable
    private static Method tryResolveAmbiguous(Method[] methods, @Nullable Class<?>[] argTypes, int count, boolean mix) {
        if (argTypes.length == 0) {
            return null;
        }
        var candidate = methods[0];
        var candidateArgs = candidate.getParameterTypes();
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
    public static Constructor<?> chooseConstructor(
            List<Constructor<?>> constructors, @Nullable Object @Nullable [] args) {
        return chooseConstructor(constructors, getArgTypes(args));
    }

    /**
     * @param constructors constructors
     * @param argTypes     if mixed, first arg is the host of member methods.
     * @return null if not found
     */
    @Nullable
    public static Constructor<?> chooseConstructor(
            List<Constructor<?>> constructors, @Nullable Class<?>[] argTypes) {
        if (constructors.isEmpty()) {
            return null;
        }
        var candidate = new Constructor[constructors.size()];
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
            var constructor = tryResolveAmbiguous(candidate, argTypes, candidateCount);
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
    private static Constructor<?> tryResolveAmbiguous(
            Constructor<?>[] constructors, @Nullable Class<?>[] argTypes, int count) {
        if (constructors.length == 0) {
            return null;
        }
        if (argTypes.length == 0) {
            return null;
        }
        var candidate = constructors[0];
        var candidateArgs = candidate.getParameterTypes();
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

    private static int getAssignCost(@Nullable Class<?>[] froms, Class<?>[] tos) {
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

    private static int getAssignCost(@Nullable Class<?> passedType, Class<?> acceptType) {
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
    public static Object invoke(Method method, @Nullable Object @Nullable [] args) {
        if (ClassUtils.isStatic(method)) {
            return invoke(method, null, args);
        }
        if (args == null || args.length == 0 || args[0] == null) {
            throw new ScriptEvaluateException("this method need one argument at least");
        }
        var methodArgs = fitArgs(args, method.getParameterCount(), 1);
        return invoke(method, args[0], methodArgs);
    }

}
