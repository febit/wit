// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.extern.lib.std;

import lombok.experimental.UtilityClass;
import org.febit.wit.Context;
import org.febit.wit.Engine;
import org.febit.wit.EngineModule;
import org.febit.wit.runtime.FunctionDeclare;
import org.jspecify.annotations.Nullable;

import static org.febit.wit.util.ArrayUtils.get;

@SuppressWarnings({
        "squid:S1172", // Unused method parameters should be removed
        "squid:S00100" // Method names should comply with a naming convention
})
public class TypesModule implements EngineModule {

    @Override
    public void apply(Engine engine) {
        var heap = engine.staticHeaps().constant();
        heap.setFunction("is_array", Types::is_array);
        heap.setFunction("is_bool", Types::is_bool);
        heap.setFunction("is_function", Types::is_function);
        heap.setFunction("is_callable", Types::is_function);
        heap.setFunction("is_null", Types::is_null);
        heap.setFunction("is_number", Types::is_number);
    }

    @UtilityClass
    static class Types {
        static boolean is_function(Context context, @Nullable Object @Nullable [] args) {
            return get(args, 0) instanceof FunctionDeclare;
        }

        static boolean is_number(Context context, @Nullable Object @Nullable [] args) {
            return get(args, 0) instanceof Number;
        }

        static boolean is_bool(Context context, @Nullable Object @Nullable [] args) {
            return get(args, 0) instanceof Boolean;
        }

        static boolean is_null(Context context, @Nullable Object @Nullable [] args) {
            return args == null
                    || args.length == 0
                    || get(args, 0) == null;
        }

        static boolean is_array(Context context, @Nullable Object @Nullable [] args) {
            final Object item = get(args, 0);
            return item != null
                    && item.getClass().isArray();
        }
    }
}
