// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.runtime.variant;

import java.util.Map;

/**
 *
 * @author Zqq
 */
public class VariantUtil {

    private final static int MAX_OF_NORMAL_VAR_MAP = Integer.MAX_VALUE;//XXX: size this smaller when real need HashVariantMap
    public final static VariantMap EMPTY = new VariantMap(new String[0]);

    public static VariantMap toVariantMap(final Map<String, Integer> map) {
        if (map == null || map.isEmpty()) {
            return EMPTY;
        } else if (map.size() <= MAX_OF_NORMAL_VAR_MAP) {
            return new VariantMap(map);
        } else {
            return new HashVariantMap(map);
        }
    }
}
