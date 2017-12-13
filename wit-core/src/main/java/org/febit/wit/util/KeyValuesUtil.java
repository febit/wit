// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.util;

import java.util.Map;
import org.febit.wit.Vars;

/**
 *
 * @author zqq90
 */
public class KeyValuesUtil {

    /**
     * @deprecated use <code>Vars.EMPTY</code> instead.
     */
    @Deprecated
    public static final Vars EMPTY_KEY_VALUES = Vars.EMPTY;

    private KeyValuesUtil() {
    }

    public static Vars wrap(final Vars v1, final Vars v2) {
        return (accepter) -> {
            v1.exportTo(accepter);
            v2.exportTo(accepter);
        };
    }

    public static Vars wrap(final Vars... valueses) {
        if (valueses == null || valueses.length == 0) {
            return Vars.EMPTY;
        }
        return (accepter) -> {
            for (Vars item : valueses) {
                item.exportTo(accepter);
            }
        };
    }

    public static Vars wrap(final String key, final Object value) {
        return (accepter) -> {
            accepter.set(key, value);
        };
    }

    public static Vars wrap(final String[] keys, final Object[] values) {
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

    public static Vars wrap(final Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            return Vars.EMPTY;
        }
        return (accepter) -> {
            map.entrySet().forEach((entry) -> {
                accepter.set(entry.getKey(), entry.getValue());
            });
        };
    }
}
