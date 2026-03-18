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
