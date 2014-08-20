// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.util;

import java.util.Map;
import webit.script.lang.KeyValueAccepter;
import webit.script.lang.KeyValues;

/**
 *
 * @author Zqq
 */
public class KeyValuesUtil {

    public final static KeyValues EMPTY_KEY_VALUES = new KeyValues() {

        public void exportTo(KeyValueAccepter accepter) {
        }
    };

    public static KeyValues wrap(final KeyValues v1, final KeyValues v2) {
        return new KeyValues() {
            public void exportTo(KeyValueAccepter accepter) {
                v1.exportTo(accepter);
                v2.exportTo(accepter);
            }
        };
    }

    public static KeyValues wrap(final KeyValues... valueses) {
        return new KeyValues() {
            public void exportTo(KeyValueAccepter accepter) {
                for (int i = 0, len = valueses.length; i < len; i++) {
                    valueses[i].exportTo(accepter);
                }
            }
        };
    }

    public static KeyValues wrap(final String key, final Object values) {
        return new KeyValues() {
            public void exportTo(KeyValueAccepter accepter) {
                accepter.set(key, values);
            }
        };
    }

    public static KeyValues wrap(final String[] keys, final Object[] values) {
        return new ArrayKeyValues(keys, values);
    }

    public static KeyValues wrap(final Map<String, Object> map) {
        if (map != null) {
            return new MapKeyValues(map);
        } else {
            return EMPTY_KEY_VALUES;
        }
    }

    private final static class MapKeyValues implements KeyValues {

        private final Map<String, Object> map;

        MapKeyValues(Map<String, Object> map) {
            this.map = map;
        }

        public void exportTo(KeyValueAccepter accepter) {
            for (Map.Entry<String, Object> entry : this.map.entrySet()) {
                accepter.set(entry.getKey(), entry.getValue());
            }
        }
    }

    private final static class ArrayKeyValues implements KeyValues {

        private final String[] keys;
        private final Object[] values;
        private final int size;

        ArrayKeyValues(String[] keys, Object[] values) {
            this.keys = keys;
            this.values = values;
            if (keys == null || values == null) {
                this.size = 0;
            } else {
                this.size = Math.min(keys.length, values.length);
            }
        }

        public void exportTo(KeyValueAccepter accepter) {
            final int mySize = this.size;
            final String[] mykeys = this.keys;
            final Object[] myValues = this.values;
            for (int i = 0; i < mySize; i++) {
                accepter.set(mykeys[i], myValues[i]);
            }
        }
    }
}
