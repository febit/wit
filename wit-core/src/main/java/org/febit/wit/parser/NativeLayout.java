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
