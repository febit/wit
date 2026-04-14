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
package org.febit.wit.runtime.accessor;

import org.febit.wit.engine.accessor.Getter;
import org.febit.wit.engine.accessor.Renderer;
import org.febit.wit.engine.accessor.Setter;
import org.febit.wit.exception.ScriptEvaluateException;
import org.febit.wit.io.Out;
import org.febit.wit.runtime.Undefined;
import org.jspecify.annotations.Nullable;

public class UndefinedAccessor implements Getter<Undefined>,
        Setter<Undefined>, Renderer<Undefined> {

    @Nullable
    @Override
    public Object get(Undefined obj, @Nullable Object property) {
        throw new ScriptEvaluateException("type 'Undefined' has no property.");
    }

    @Override
    public void set(Undefined obj, @Nullable Object property, @Nullable Object value) {
        throw new ScriptEvaluateException("type 'Undefined' has no property.");
    }

    @Override
    public void render(Out out, Undefined obj) {
        // nothing to render
    }

}
