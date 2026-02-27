package org.febit.wit.parser;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.febit.wit.runtime.WitFunction;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Accessors(fluent = true)
@RequiredArgsConstructor(staticName = "of")
public class CachingNativeFunctionFactory implements NativeFunctionFactoryDecorator {

    protected final ConcurrentMap<Object, WitFunction> cache = new ConcurrentHashMap<>();

    @Getter
    private final NativeFunctionFactory delegate;

    @Override
    public WitFunction method(Method method) {
        return cache.computeIfAbsent(method,
                m -> delegate().method(method));
    }

    @Override
    public WitFunction constructor(Constructor<?> constructor) {
        return cache.computeIfAbsent(constructor,
                c -> delegate().constructor(constructor));
    }

}
