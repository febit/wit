// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.util;

import java.util.Map;
import org.febit.wit.Vars;

/**
 *
 * @author zqq90
 * @deprecated use <code>Vars.of(..)</code> instead.
 */
@Deprecated
public class KeyValuesUtil {

    public static final Vars EMPTY_KEY_VALUES = Vars.EMPTY;

    private KeyValuesUtil() {
    }

    public static Vars wrap(final Vars v1, final Vars v2) {
        return Vars.of(v1, v2);
    }

    public static Vars wrap(final Vars... values) {
        return Vars.of(values);
    }

    public static Vars wrap(final String key, final Object value) {
        return Vars.of(key, value);
    }

    public static Vars wrap(final String[] keys, final Object[] values) {
        return Vars.of(keys, values);
    }

    public static Vars wrap(final Map<String, Object> map) {
        return Vars.of(map);
    }
}
