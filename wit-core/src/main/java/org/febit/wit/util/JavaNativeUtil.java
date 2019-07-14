// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.util;

import org.febit.wit.Engine;
import org.febit.wit.InternalContext;
import org.febit.wit.core.NativeFactory;
import org.febit.wit.exceptions.AmbiguousMethodException;
import org.febit.wit.exceptions.ScriptRuntimeException;
import org.febit.wit.exceptions.UncheckedException;
import org.febit.wit.global.GlobalManager;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zqq90
 */
@SuppressWarnings({
        "WeakerAccess"
})
public class JavaNativeUtil {

    private static final int COST_NEVER = -1;
    private static final int COST_EXACT = 0;
    private static final int COST_ASSIGNABLE = 1;
    private static final int COST_OBJECT = 100;
    private static final int COST_PRIMITIVE = 10;
    // XXX COST_CONVERT = 1000
    private static final int COST_NULL = 1000000;

    private static final Class<?>[] EMPTY_CLASSES = new Class<?>[0];

    private JavaNativeUtil() {
    }

    public static int addStaticMethods(Engine engine, Class<?> type) {
        return addStaticMethods(engine.getGlobalManager(), engine.getNativeFactory(), type, false);
    }

    public static int addStaticMethods(Engine engine, Class<?> type, boolean skipConflict) {
        return addStaticMethods(engine.getGlobalManager(), engine.getNativeFactory(), type, skipConflict);
    }

    public static int addStaticMethods(GlobalManager manager,
                                       NativeFactory nativeFactory,
                                       Class<?> type) {
        return addStaticMethods(manager, nativeFactory, type, false);
    }

    public static int addStaticMethods(
            GlobalManager manager,
            NativeFactory nativeFactory,
            Class<?> type,
            boolean skipConflict
    ) {
        Map<String, List<Method>> methodMap = Arrays.stream(type.getMethods())
                .filter(ClassUtil::isStatic)
                .filter(m -> !(skipConflict && manager.hasConst(m.getName())))
                .collect(Collectors.groupingBy(Method::getName));

        methodMap.forEach((name, methods) -> manager.setConst(name,
                nativeFactory.createNativeMethodDeclare(methods)));
        return methodMap.size();
    }

    public static int addConstFields(Engine engine, Class<?> type) {
        return addConstFields(engine.getGlobalManager(), engine.getNativeFactory(), type, false);
    }

    public static int addConstFields(Engine engine, Class<?> type, boolean skipConflict) {
        return addConstFields(engine.getGlobalManager(), engine.getNativeFactory(), type, skipConflict);
    }

    public static int addConstFields(
            GlobalManager manager,
            NativeFactory nativeFactory,
            Class<?> type
    ) {
        return addConstFields(manager, nativeFactory, type, false);
    }

    public static int addConstFields(
            GlobalManager manager,
            NativeFactory nativeFactory,
            Class<?> type,
            boolean skipConflict
    ) {
        int count = 0;
        for (Field field : type.getFields()) {
            if (!ClassUtil.isStatic(field)
                    || !ClassUtil.isFinal(field)) {
                continue;
            }
            String name = field.getName();
            if (skipConflict && manager.hasConst(name)) {
                continue;
            }
            ClassUtil.setAccessible(field);
            try {
                manager.setConst(name, field.get(null));
            } catch (IllegalArgumentException | IllegalAccessException e) {
                throw new UncheckedException(e);
            }
        }
        return count;
    }

    public static Class<?>[] getArgTypes(Object[] args) {
        if (args == null || args.length == 0) {
            return EMPTY_CLASSES;
        }
        Class<?>[] argTypes = new Class[args.length];
        for (int i = 0; i < argTypes.length; i++) {
            argTypes[i] = args[i] != null ? args[i].getClass() : null;
        }
        return argTypes;
    }

    /**
     * @param methods
     * @param args
     * @return null if not found
     */
    public static Method getMatchMethod(Method[] methods, Object[] args) {
        return getMatchMethod(methods, getArgTypes(args));
    }

    /**
     * @param methods
     * @param args    if mixed, first arg is the host of member methods.
     * @param mix     if mix, static methods with member methods
     * @return null if not found
     */
    public static Method getMatchMethod(Method[] methods, Object[] args, boolean mix) {
        return getMatchMethod(methods, getArgTypes(args), mix);
    }

    /**
     * @param methods
     * @param argTypes
     * @return null if not found
     */
    public static Method getMatchMethod(Method[] methods, Class<?>[] argTypes) {
        return getMatchMethod(methods, argTypes, false);
    }

    /**
     * @param methods
     * @param argTypes if mixed, first arg is the host of member methods.
     * @param mix      if mix static methods with member methods
     * @return null if not found
     */
    public static Method getMatchMethod(Method[] methods, Class<?>[] argTypes, boolean mix) {
        if (methods == null
                || methods.length == 0) {
            return null;
        }
        Method[] candidate = new Method[methods.length];
        int candidateCount = 0;
        int leastCost = Integer.MAX_VALUE;
        Class<?>[] argTypesForMemberMethods = null;
        for (Method method : methods) {
            int cost;
            if (mix && !ClassUtil.isStatic(method)) {
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
     * @param argTypes
     * @param methods
     * @param count
     * @param mix
     * @return null if can't resolve
     */
    protected static Method resolveAmbiguousMethods(Class<?>[] argTypes, Method[] methods, int count, boolean mix) {
        if (argTypes.length == 0) {
            return null;
        }
        Method candidate = methods[0];
        Class<?>[] candidateArgs = candidate.getParameterTypes();
        for (int i = 1; i < count; i++) {
            Method next = methods[i];
            if (mix && ClassUtil.isStatic(candidate) != ClassUtil.isStatic(next)) {
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
     * @param constructors
     * @param args
     * @return null if not found
     */
    public static Constructor getMatchConstructor(Constructor[] constructors, Object[] args) {
        return getMatchConstructor(constructors, getArgTypes(args));
    }

    /**
     * @param constructors
     * @param argTypes     if mixed, first arg is the host of member methods.
     * @return null if not found
     */
    public static Constructor getMatchConstructor(Constructor[] constructors, Class<?>[] argTypes) {
        if (constructors == null
                || constructors.length == 0) {
            return null;
        }
        Constructor[] candidate = new Constructor[constructors.length];
        int candidateCount = 0;
        int leastCost = Integer.MAX_VALUE;
        for (Constructor constructor : constructors) {
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
            Constructor constructor = resolveAmbiguousConstructors(argTypes, candidate, candidateCount);
            if (constructor != null) {
                return constructor;
            }
            throw new AmbiguousMethodException(
                    Arrays.copyOf(candidate, candidateCount),
                    argTypes);
        }
        return candidate[0];
    }

    protected static Constructor resolveAmbiguousConstructors(Class<?>[] argTypes,
                                                              Constructor[] constructors, int count) {
        if (argTypes.length == 0) {
            return null;
        }
        Constructor candidate = constructors[0];
        Class<?>[] candidateArgs = candidate.getParameterTypes();
        for (int i = 1; i < count; i++) {
            Constructor next = constructors[i];
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

    protected static int getAssignCost(Class<?>[] froms, Class<?>[] tos) {
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

    protected static int getAssignCost(Class<?> passedType, Class<?> acceptType) {
        if (passedType == null) {
            return acceptType.isPrimitive() ? COST_NEVER : COST_NULL;
        }
        if (passedType.equals(acceptType)) {
            return COST_EXACT;
        }
        if (acceptType.isPrimitive()) {
            return passedType == ClassUtil.getBoxedPrimitiveClass(acceptType)
                    ? COST_PRIMITIVE
                    : COST_NEVER;
        }
        if (passedType.isPrimitive()) {
            return acceptType == ClassUtil.getBoxedPrimitiveClass(passedType)
                    ? COST_PRIMITIVE
                    : COST_NEVER;
        }
        if (acceptType.isAssignableFrom(passedType)) {
            return acceptType == Object.class ? COST_OBJECT : COST_ASSIGNABLE;
        }
        //TODO: support auto convert
        return COST_NEVER;
    }

    public static Object invokeMethod(
            final Method method,
            final Object[] args
    ) {
        if (ClassUtil.isStatic(method)) {
            return invokeMethod(method, null, args);
        }
        if (args == null || args.length == 0 || args[0] == null) {
            throw new ScriptRuntimeException("this method need one argument at least");
        }
        final Object[] methodArgs = prepareArgs(method.getParameterCount(), args, 1);
        return invokeMethod(method, args[0], methodArgs);
    }

    public static Object[] prepareArgs(final int acceptArgsCount, final Object[] args, final int from) {
        if (args == null) {
            return acceptArgsCount == 0
                    ? ArrayUtil.emptyObjects()
                    : new Object[acceptArgsCount];
        }
        if (from == 0 && args.length == acceptArgsCount) {
            return args;
        }
        final Object[] result = new Object[acceptArgsCount];
        System.arraycopy(args, from, result, 0, Math.min(args.length - from, acceptArgsCount));
        return result;
    }

    public static Object invokeMethod(
            final Method method,
            final Object me,
            final Object[] args
    ) {
        final Object[] methodArgs = prepareArgs(method.getParameterCount(), args, 0);
        try {
            Object result = method.invoke(me, methodArgs);
            return ClassUtil.isVoidType(method.getReturnType())
                    ? InternalContext.VOID
                    : result;
        } catch (IllegalAccessException ex) {
            throw new ScriptRuntimeException("this method is inaccessible: ".concat(ex.getLocalizedMessage()), ex);
        } catch (IllegalArgumentException ex) {
            throw new ScriptRuntimeException("illegal argument: ".concat(ex.getLocalizedMessage()), ex);
        } catch (InvocationTargetException ex) {
            throw new ScriptRuntimeException("this method throws an exception", ex);
        }
    }

    public static Object invokeConstructor(final Constructor constructor, final Object[] args) {
        final Object[] methodArgs = prepareArgs(constructor.getParameterCount(), args, 0);
        try {
            return constructor.newInstance(methodArgs);
        } catch (InstantiationException ex) {
            throw new ScriptRuntimeException("Can't create new instance: ".concat(ex.getLocalizedMessage()), ex);
        } catch (IllegalAccessException ex) {
            throw new ScriptRuntimeException("Inaccessible method: ".concat(ex.getLocalizedMessage()), ex);
        } catch (IllegalArgumentException ex) {
            throw new ScriptRuntimeException("Illegal arguments: ".concat(ex.getLocalizedMessage()), ex);
        } catch (InvocationTargetException ex) {
            throw new ScriptRuntimeException("this method throws an exception", ex);
        }
    }

}
