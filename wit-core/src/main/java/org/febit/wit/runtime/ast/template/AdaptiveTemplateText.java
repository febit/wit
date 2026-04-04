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
package org.febit.wit.runtime.ast.template;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.febit.wit.runtime.RuntimeContext;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.ast.Statement;
import org.jspecify.annotations.Nullable;

import java.nio.charset.Charset;

@Accessors(fluent = true)
@RequiredArgsConstructor
public final class AdaptiveTemplateText implements Statement {

    private final char[] chars;
    private final byte[] encoded;
    private final Charset charset;
    @Getter
    private final Position position;

    @Nullable
    @Override
    public Object execute(RuntimeContext context) {
        var out = context.out();
        if (out.preferBytes()
                && charset.equals(out.charset())) {
            out.write(encoded);
        } else {
            out.write(chars);
        }
        return null;
    }
}
