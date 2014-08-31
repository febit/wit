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

    public static final KeyValues EMPTY_KEY_VALUES = new KeyValues() {

        public void exportTo(KeyValueAccepter accepter) {
        }
    };

    public static KeyValues wrap(final KeyValues v1, final KeyValues v2) {
        return new TwoKeyValues(v1, v2);
    }

    public static KeyValues wrap(final KeyValues... valueses) {
        if (valueses != null) {
            return new ListKeyValues(valueses);
        } 
            return EMPTY_KEY_VALUES;
    }

    public static KeyValues wrap(final String key, final Object value) {
        return new OneKeyValues(key, value);
    }

    public static KeyValues wrap(final String[] keys, final Object[] values) {
        if (keys != null && values != null) {
            return new ArrayKeyValues(keys, values);
        } 
            return EMPTY_KEY_VALUES;
    }

    public static KeyValues wrap(final Map<String, Object> map) {
        if (map != null && !map.isEmpty()) {
            return new MapKeyValues(map);
        }
            return EMPTY_KEY_VALUES;
    }

    private static final class OneKeyValues implements KeyValues {

        private final String key;
        private final Object value;

        OneKeyValues(String key, Object value) {
            this.key = key;
            this.value = value;
        }

        public void exportTo(KeyValueAccepter accepter) {
            accepter.set(key, value);
        }
    }

    private static final class TwoKeyValues implements KeyValues {

        private final KeyValues v1;
        private final KeyValues v2;

        TwoKeyValues(KeyValues v1, KeyValues v2) {
            this.v1 = v1;
            this.v2 = v2;
        }

        public void exportTo(KeyValueAccepter accepter) {
            v1.exportTo(accepter);
            v2.exportTo(accepter);
        }
    }

    private static final class ListKeyValues implements KeyValues {

        private final KeyValues[] array;

        ListKeyValues(KeyValues[] array) {
            this.array = array;
        }

        public void exportTo(KeyValueAccepter accepter) {
            for (KeyValues item : this.array) {
                item.exportTo(accepter);
            }
        }
    }

    private static final class MapKeyValues implements KeyValues {

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

    private static final class ArrayKeyValues implements KeyValues {

        private final String[] keys;
        private final Object[] values;
        private final int size;

        ArrayKeyValues(String[] keys, Object[] values) {
            this.keys = keys;
            this.values = values;
            this.size = Math.min(keys.length, values.length);
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
