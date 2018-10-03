// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit;

import java.util.Map;

/**
 * @author zqq90
 * @since 2.4.0
 */
@FunctionalInterface
public interface Vars {

    Vars EMPTY = accepter -> {
        // Do nothing
    };

    @FunctionalInterface
    interface Accepter {

        default void set(Object key, Object value) {
            set(String.valueOf(key), value);
        }

        void set(String key, Object value);
    }

    /**
     * @since 2.5.0
     */
    static Vars of(final Vars v1, final Vars v2) {
        return accepter -> {
            v1.exportTo(accepter);
            v2.exportTo(accepter);
        };
    }

    /**
     * @since 2.5.0
     */
    static Vars of(final Vars... values) {
        if (values == null || values.length == 0) {
            return Vars.EMPTY;
        }
        return accepter -> {
            for (Vars item : values) {
                item.exportTo(accepter);
            }
        };
    }

    /**
     * @since 2.5.0
     */
    static Vars of(final String key, final Object value) {
        return accepter -> accepter.set(key, value);
    }

    /**
     * @since 2.5.0
     */
    static Vars of(final String[] keys, final Object[] values) {
        if (keys == null || values == null) {
            return Vars.EMPTY;
        }
        final int size = Math.min(keys.length, values.length);
        if (size == 0) {
            return Vars.EMPTY;
        }
        return accepter -> {
            for (int i = 0; i < size; i++) {
                accepter.set(keys[i], values[i]);
            }
        };
    }

    /**
     * @since 2.5.0
     */
    static Vars of(final Map<?, ?> map) {
        if (map == null || map.isEmpty()) {
            return Vars.EMPTY;
        }
        return accepter -> map.forEach(accepter::set);
    }

    void exportTo(Accepter accepter);

}
