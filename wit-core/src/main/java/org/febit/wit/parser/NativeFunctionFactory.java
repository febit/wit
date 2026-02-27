package org.febit.wit.parser;

import org.febit.wit.runtime.Function;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

public interface NativeFunctionFactory {

    Function array(Class<?> componentType);

    Function method(Method method);

    Function method(List<Method> methods);

    Function constructor(Constructor<?> constructor);

    Function constructor(List<Constructor<?>> constructors);

    default NativeFunctionFactory withCache() {
        return CachingNativeFunctionFactory.of(this);
    }

}
