package org.febit.wit.parser;

import org.febit.wit.runtime.Function;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

public interface NativeFunctionFactoryDecorator extends NativeFunctionFactory {
    NativeFunctionFactory delegate();

    @Override
    default Function array(Class<?> componentType) {
        return delegate().array(componentType);
    }

    @Override
    default Function method(Method method) {
        return delegate().method(method);
    }

    @Override
    default Function method(List<Method> methods) {
        return delegate().method(methods);
    }

    @Override
    default Function constructor(Constructor<?> constructor) {
        return delegate().constructor(constructor);
    }

    @Override
    default Function constructor(List<Constructor<?>> constructors) {
        return delegate().constructor(constructors);
    }
}
