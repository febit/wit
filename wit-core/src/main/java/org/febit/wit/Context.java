// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit;

import org.febit.wit.exceptions.NotFunctionException;
import org.febit.wit.exceptions.ScriptRuntimeException;
import org.febit.wit.runtime.heap.LocalHeap;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Runtime context.
 *
 */
@SuppressWarnings({
        "squid:S1214", //Constants should not be defined in interfaces
        "squid:RedundantThrowsDeclarationCheck"
})
public interface Context {

    Heap heap();

    LocalHeap local();

    Function exportFunction(String name) throws NotFunctionException;

    interface Heap {
        void set(String name, @Nullable Object value);

        @Nullable
        Object get(String name, boolean force) throws ScriptRuntimeException;

        @Nullable
        default Object get(String name) throws ScriptRuntimeException {
            return get(name, true);
        }

        void each(BiConsumer<String, @Nullable Object> action);

        default void exportTo(Map<? super String, @Nullable Object> map) {
            each(map::put);
        }
    }
}
