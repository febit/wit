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
package org.febit.wit.extern.lib.std;

import lombok.experimental.UtilityClass;
import org.febit.wit.Wit;
import org.febit.wit.WitModule;
import org.febit.wit.runtime.WitFunction;
import org.jspecify.annotations.Nullable;

import static org.febit.wit.util.Args.at;

@SuppressWarnings({
        "squid:S1172", // Unused method parameters should be removed
        "squid:S00100" // Method names should comply with a naming convention
})
public class TypesModule implements WitModule {

    @Override
    public void apply(Wit wit) {
        var heap = wit.globals().constants();
        heap.setAsFunction("is_array", Types::is_array);
        heap.setAsFunction("is_bool", Types::is_bool);
        heap.setAsFunction("is_function", Types::is_function);
        heap.setAsFunction("is_callable", Types::is_function);
        heap.setAsFunction("is_null", Types::is_null);
        heap.setAsFunction("is_number", Types::is_number);
    }

    @UtilityClass
    static class Types {
        static boolean is_function(@Nullable Object @Nullable [] args) {
            return at(args, 0) instanceof WitFunction;
        }

        static boolean is_number(@Nullable Object @Nullable [] args) {
            return at(args, 0) instanceof Number;
        }

        static boolean is_bool(@Nullable Object @Nullable [] args) {
            return at(args, 0) instanceof Boolean;
        }

        static boolean is_null(@Nullable Object @Nullable [] args) {
            return args == null
                    || args.length == 0
                    || at(args, 0) == null;
        }

        static boolean is_array(@Nullable Object @Nullable [] args) {
            final Object item = at(args, 0);
            return item != null
                    && item.getClass().isArray();
        }
    }
}
