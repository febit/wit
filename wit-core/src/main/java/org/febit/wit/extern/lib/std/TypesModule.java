// Copyright (c) 2013-present, febit.org. All Rights Reserved.
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
        var heap = wit.staticHeaps().constants();
        heap.setFunction("is_array", Types::is_array);
        heap.setFunction("is_bool", Types::is_bool);
        heap.setFunction("is_function", Types::is_function);
        heap.setFunction("is_callable", Types::is_function);
        heap.setFunction("is_null", Types::is_null);
        heap.setFunction("is_number", Types::is_number);
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
