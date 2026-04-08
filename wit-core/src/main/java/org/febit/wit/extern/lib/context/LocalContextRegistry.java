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

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.febit.wit.Wit;
import org.febit.wit.WitModule;
import org.febit.wit.exception.ScriptEvaluateException;
import org.febit.wit.runtime.RuntimeContext;
import org.jspecify.annotations.Nullable;

@Accessors(fluent = true)
@RequiredArgsConstructor(staticName = "create")
public class LocalContextRegistry implements WitModule {

    public static final String DEFAULT_NAME = "$LOCAL";

    @Getter
    private final String name;

    public static LocalContextRegistry create() {
        return create(DEFAULT_NAME);
    }

    @Override
    public void apply(Wit wit) {
        var heap = wit.globals().constants();
        heap.setAsFunction(this.name, LocalContextRegistry::local);
    }

    @Nullable
    public static Object local(RuntimeContext context, @Nullable Object @Nullable [] args) {
        final int len = args == null ? 0 : args.length;
        if (args == null || len < 1) {
            throw new ScriptEvaluateException("One more arguments expected for local context function");
        }
        var arg0 = args[0];
        if (arg0 == null) {
            throw new ScriptEvaluateException("Key of local context can not be null");
        }
        var key = arg0.toString();
        if (len == 1) {
            return context.local().get(key);
        }
        context.local().set(key, args[1]);
        return args[1];
    }
}
