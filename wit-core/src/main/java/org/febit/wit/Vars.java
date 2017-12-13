// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit;

import java.util.Map;

/**
 *
 * @author zqq90
 * @since 2.4.0
 */
@FunctionalInterface
public interface Vars {

    public static final Vars EMPTY = (accepter) -> {
        // Do nothing
    };

    @FunctionalInterface
    public static interface Accepter {

        void set(String key, Object value);
    }

    public static Vars of(final Vars v1, final Vars v2) {
        return (accepter) -> {
            v1.exportTo(accepter);
            v2.exportTo(accepter);
        };
    }

    public static Vars of(final Vars... values) {
        if (values == null || values.length == 0) {
            return Vars.EMPTY;
        }
        return (accepter) -> {
            for (Vars item : values) {
                item.exportTo(accepter);
            }
        };
    }

    public static Vars of(final String key, final Object value) {
        return (accepter) -> {
            accepter.set(key, value);
        };
    }

    public static Vars of(final String[] keys, final Object[] values) {
        if (keys == null || values == null) {
            return Vars.EMPTY;
        }
        final int size = Math.min(keys.length, values.length);
        if (size == 0) {
            return Vars.EMPTY;
        }
        return (accepter) -> {
            final String[] mykeys = keys;
            final Object[] myValues = values;
            for (int i = 0; i < size; i++) {
                accepter.set(mykeys[i], myValues[i]);
            }
        };
    }

    public static Vars of(final Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            return Vars.EMPTY;
        }
        return (accepter) -> {
            map.entrySet().forEach((entry) -> {
                accepter.set(entry.getKey(), entry.getValue());
            });
        };
    }

    void exportTo(Accepter accepter);

}
