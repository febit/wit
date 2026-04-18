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
package org.febit.wit.extern.lib.context;

import org.febit.wit.engine.Heap;
import org.febit.wit.engine.WitFunction;
import org.febit.wit.runtime.RuntimeContext;
import org.febit.wit.runtime.RuntimeReference;
import org.jspecify.annotations.Nullable;

import java.io.Serial;
import java.io.Serializable;

public class LocalContextReference implements
        RuntimeReference<Heap>, WitFunction, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public Heap get(RuntimeContext context) {
        return context.local();
    }

    @Nullable
    @Override
    public Object apply(RuntimeContext context, @Nullable Object @Nullable [] args) {
        return context.local().apply(context, args);
    }
}
