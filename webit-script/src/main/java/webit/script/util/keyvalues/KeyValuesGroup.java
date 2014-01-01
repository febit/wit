// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.util.keyvalues;

import java.util.ArrayList;
import java.util.Map;

/**
 *
 * @author Zqq
 */
public class KeyValuesGroup implements KeyValues {

    private static final int DEFAULT_CAPACITY = 8;
    private ArrayList<KeyValues> list;

    public KeyValuesGroup() {
        this(DEFAULT_CAPACITY);
    }

    public KeyValuesGroup(int capacity) {
        this.list = new ArrayList<KeyValues>(capacity);
    }

    public KeyValuesGroup(KeyValues... keyValueses) {
        this();
        this.add(keyValueses);
    }

    public final KeyValuesGroup add(Map<String, Object> map) {
        if (map != null) {
            this.list.add(KeyValuesUtil.wrap(map));
        }
        return this;
    }

    public final KeyValuesGroup add(String key, Object value) {
        this.list.add(KeyValuesUtil.wrap(key, value));
        return this;
    }

    public final KeyValuesGroup add(String[] keys, Object[] values) {
        this.list.add(KeyValuesUtil.wrap(keys, values));
        return this;
    }

    public final KeyValuesGroup add(KeyValues keyValues) {
        if (keyValues != null) {
            this.list.add(keyValues);
        }
        return this;
    }

    public final KeyValuesGroup add(KeyValues... keyValueses) {
        if (keyValueses != null) {
            final ArrayList<KeyValues> myList = this.list;
            KeyValues keyValues;
            for (int i = 0, len = keyValueses.length; i < len; i++) {
                if ((keyValues = keyValueses[i]) != null) {
                    myList.add(keyValues);
                }
            }
        }
        return this;
    }

    public void exportTo(KeyValueAccepter accepter) {
        final ArrayList<KeyValues> myList = this.list;
        for (int i = 0, len = myList.size(); i < len; i++) {
            myList.get(i).exportTo(accepter);
        }
    }
}
