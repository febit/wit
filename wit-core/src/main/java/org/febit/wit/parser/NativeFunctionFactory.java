package org.febit.wit.parser;

import org.febit.wit.runtime.WitFunction;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

public interface NativeFunctionFactory {

    WitFunction array(Class<?> componentType);

    WitFunction method(Method method);

    WitFunction method(List<Method> methods);

    WitFunction constructor(Constructor<?> constructor);

    WitFunction constructor(List<Constructor<?>> constructors);

    default NativeFunctionFactory withCache() {
        return CachingNativeFunctionFactory.of(this);
    }

}
