// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.util;

import java.util.Map;
import webit.script.core.runtime.variant.HashVariantMap;
import webit.script.core.runtime.variant.VariantMap;

/**
 *
 * @author Zqq
 */
public class VariantUtil {

    private final static int MAX_OF_NORMAL_VAR_MAP = Integer.MAX_VALUE;//XXX: resize this smaller when real need HashVariantMap

    public static VariantMap toVariantMap(final Map<String, Integer> map) {
        if (map == null || map.isEmpty()) {
            return VariantMap.EMPTY;
        } else if (map.size() <= MAX_OF_NORMAL_VAR_MAP) {
            return new VariantMap(map);
        } else {
            return new HashVariantMap(map);
        }
    }
}
