/*
 * Copyright 2013-present febit.org (support@febit.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.febit.wit.extern.lib.tld;

import lombok.extern.slf4j.Slf4j;
import org.febit.wit.Wit;
import org.febit.wit.WitModule;
import org.febit.wit.exception.ParseException;
import org.febit.wit.exception.UncheckedException;
import org.febit.wit.parser.NativeLayout;
import org.febit.wit.runtime.WitFunction;
import org.febit.wit.runtime.ast.TextPosition;
import org.febit.wit.util.ClassUtils;
import org.febit.wit.util.PathUtils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Method;
import java.util.List;

@Slf4j
@lombok.Builder(
        builderClassName = "Builder"
)
public class TldModule implements WitModule {

    @lombok.NonNull
    @SuppressWarnings("NullableProblems")
    private final String path;

    @lombok.Builder.Default
    private final String prefix = "";
    @lombok.Builder.Default
    private final boolean checkAccess = false;

    @Override
    public void apply(Wit wit) {
        var heaps = wit.staticHeaps();
        var nativeLayout = wit.nativeLayout();

        log.info("Load TLD file: {}", path);
        var input = ClassUtils.classLoader()
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
            heaps.constants().set(
                    this.prefix + func.name(),
                    createFunction(nativeLayout, func)
            );
        }
    }

    protected WitFunction createFunction(NativeLayout nativeLayout, TldFunction func) {

        if (checkAccess) {
            nativeLayout.securityCheck(
                    func.declaredClass() + '.' + func.methodName(),
                    TextPosition.UNKNOWN
            );
        }

        var paramTypes = func.parameterTypes().stream()
                .map(ClassUtils::loadByName)
                .toArray(Class<?>[]::new);

        var clazz = ClassUtils.loadByName(func.declaredClass());

        Method method;
        try {
            method = clazz.getMethod(func.methodName(), paramTypes);
        } catch (NoSuchMethodException | SecurityException ex) {
            throw new ParseException(ex.getMessage(), ex, TextPosition.UNKNOWN);
        }

        return nativeLayout.functions().method(method);
    }
}
