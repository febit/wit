package org.febit.wit.parser;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.febit.wit.runtime.Function;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Accessors(fluent = true)
@RequiredArgsConstructor(staticName = "of")
public class CachingNativeFunctionFactory implements NativeFunctionFactoryDecorator {

    protected final ConcurrentMap<Object, Function> cache = new ConcurrentHashMap<>();

    @Getter
    private final NativeFunctionFactory delegate;

    @Override
    public Function method(Method method) {
        return cache.computeIfAbsent(method,
                m -> delegate().method(method));
    }

    @Override
    public Function constructor(Constructor<?> constructor) {
        return cache.computeIfAbsent(constructor,
                c -> delegate().constructor(constructor));
    }

}
