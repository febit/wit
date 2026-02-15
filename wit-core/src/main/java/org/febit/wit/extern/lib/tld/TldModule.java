// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.extern.lib.tld;

import lombok.extern.slf4j.Slf4j;
import org.febit.wit.Engine;
import org.febit.wit.EngineModule;
import org.febit.wit.core.NativeFactory;
import org.febit.wit.exception.UncheckedException;
import org.febit.wit.runtime.function.FunctionDeclare;
import org.febit.wit.util.ClassUtils;
import org.febit.wit.util.PathUtils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

@Slf4j
@lombok.Builder(
        builderClassName = "Builder"
)
public class TldModule implements EngineModule {

    @lombok.NonNull
    @SuppressWarnings("NullableProblems")
    private final String path;

    @lombok.Builder.Default
    private final String prefix = "";
    @lombok.Builder.Default
    private final boolean checkAccess = false;

    @Override
    public void apply(Engine engine) {
        var heaps = engine.staticHeaps();
        var nativeFactory = engine.nativeFactory();

        log.info("Load TLD file: {}", path);
        var input = ClassUtils.getDefaultClassLoader()
                .getResourceAsStream(PathUtils.concat("META-INF/", path));
        if (input == null) {
            throw new UncheckedIOException(new IOException("TLD file not found: " + path));
        }

        List<TldFunction> functions;
        try (input) {
            functions = TldFunctionsParser.parse(input);
        } catch (Exception e) {
            throw new UncheckedException(e);
        }
        for (var func : functions) {
            heaps.constant().set(
                    this.prefix + func.name(),
                    createFunction(nativeFactory, func)
            );
        }
    }

    protected FunctionDeclare createFunction(NativeFactory nativeFactory, TldFunction func) {
        var parameterTypes = func.parameterTypes().stream()
                .map(ClassUtils::loadByName)
                .toArray(Class<?>[]::new);

        return nativeFactory.getNativeMethodDeclare(
                ClassUtils.loadByName(func.declaredClass()),
                func.methodName(),
                parameterTypes,
                checkAccess);
    }
}
