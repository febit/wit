// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.util;

import java.util.Map;
import org.febit.wit.lang.KeyValueAccepter;
import org.febit.wit.lang.KeyValues;

/**
 *
 * @author zqq90
 */
public class KeyValuesUtil {

    /**
     * @deprecated use <code>KeyValues.EMPTY</code> instead.
     */
    @Deprecated
    public static final KeyValues EMPTY_KEY_VALUES = KeyValues.EMPTY;

    private KeyValuesUtil() {
    }

    public static KeyValues wrap(final KeyValues v1, final KeyValues v2) {
        return new KeyValues() {
            @Override
            public void exportTo(KeyValueAccepter accepter) {
                v1.exportTo(accepter);
                v2.exportTo(accepter);
            }
        };
    }

    public static KeyValues wrap(final KeyValues... valueses) {
        if (valueses == null || valueses.length == 0) {
            return KeyValues.EMPTY;
        }
        return new KeyValues() {
            @Override
            public void exportTo(KeyValueAccepter accepter) {
                for (KeyValues item : valueses) {
                    item.exportTo(accepter);
                }
            }
        };
    }

    public static KeyValues wrap(final String key, final Object value) {
        return new KeyValues() {
            @Override
            public void exportTo(KeyValueAccepter accepter) {
                accepter.set(key, value);
            }
        };
    }

    public static KeyValues wrap(final String[] keys, final Object[] values) {
        if (keys == null || values == null) {
            return KeyValues.EMPTY;
        }
        final int size = Math.min(keys.length, values.length);
        if (size == 0) {
            return KeyValues.EMPTY;
        }
        return new KeyValues() {
            @Override
            public void exportTo(KeyValueAccepter accepter) {
                final String[] mykeys = keys;
                final Object[] myValues = values;
                for (int i = 0; i < size; i++) {
                    accepter.set(mykeys[i], myValues[i]);
                }
            }
        };
    }

    public static KeyValues wrap(final Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            return KeyValues.EMPTY;
        }
        return new KeyValues() {
            @Override
            public void exportTo(KeyValueAccepter accepter) {
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    accepter.set(entry.getKey(), entry.getValue());
                }
            }
        };
    }
}
