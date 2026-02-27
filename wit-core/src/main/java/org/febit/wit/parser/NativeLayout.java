// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.parser;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.febit.wit.exception.ParseException;
import org.febit.wit.parser.security.NativeSecurity;
import org.febit.wit.parser.security.NoopNativeSecurity;
import org.febit.wit.runtime.ast.Position;

@Slf4j
@Accessors(fluent = true)
@lombok.Builder(
        builderClassName = "Builder"
)
public class NativeLayout {

    @Getter
    @lombok.Builder.Default
    private final NativeSecurity security = NoopNativeSecurity.INSTANCE;

    @Getter
    @lombok.Builder.Default
    private final NativeFunctionFactory functions = ReflectNativeFunctionFactory.INSTANCE.withCache();

    public static NativeLayout ofDefault() {
        return builder().build();
    }

    public void securityCheck(String path, Position position) {
        if (!security().allowed(path)) {
            throw new ParseException("Not accessible of native path: " + path, position);
        }
    }

}
