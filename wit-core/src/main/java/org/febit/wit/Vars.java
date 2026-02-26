// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit;

import org.jspecify.annotations.Nullable;

import java.util.Map;

@FunctionalInterface
public interface Vars {

    @FunctionalInterface
    interface Acceptor {

        default void set(Object key, @Nullable Object value) {
            set(String.valueOf(key), value);
        }

        void set(String key, @Nullable Object value);
    }

    static Vars empty() {
        return acceptor -> {
            // Do nothing
        };
    }

    static Vars concat(Vars v1, Vars v2) {
        return acceptor -> {
            v1.sink(acceptor);
            v2.sink(acceptor);
        };
    }

    static Vars concat(Vars @Nullable ... values) {
        if (values == null || values.length == 0) {
            return Vars.empty();
        }
        return acceptor -> {
            for (Vars item : values) {
                item.sink(acceptor);
            }
        };
    }

    static Vars of(String key, @Nullable Object value) {
        return acceptor -> acceptor.set(key, value);
    }

    static Vars of(String @Nullable [] keys, @Nullable Object @Nullable [] values) {
        if (keys == null || values == null) {
            return Vars.empty();
        }
        final int size = Math.min(keys.length, values.length);
        if (size == 0) {
            return Vars.empty();
        }
        return acceptor -> {
            for (int i = 0; i < size; i++) {
                acceptor.set(keys[i], values[i]);
            }
        };
    }

    static Vars of(@Nullable Map<?, ?> map) {
        if (map == null || map.isEmpty()) {
            return Vars.empty();
        }
        return acceptor -> map.forEach(acceptor::set);
    }

    void sink(Acceptor acceptor);

}
