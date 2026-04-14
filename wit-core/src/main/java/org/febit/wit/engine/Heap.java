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
package org.febit.wit.engine;

import org.febit.wit.exception.ScriptEvaluateException;
import org.jspecify.annotations.Nullable;

import java.util.function.BiConsumer;

public interface Heap {

    boolean has(String name);

    void set(String name, @Nullable Object value);

    @Nullable
    Object get(String name, boolean strict) throws ScriptEvaluateException;

    void clear();

    @Nullable
    default Object get(String name) throws ScriptEvaluateException {
        return get(name, true);
    }

    void forEach(BiConsumer<String, @Nullable Object> action);

    default void setAsFunction(String key, WitFunction func) {
        set(key, func);
    }

    default void setAsFunction(String key, WitFunction.Constable func) {
        set(key, func);
    }
}
