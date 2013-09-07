// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.runtime.variant;

import java.util.Map;

/**
 *
 * @author Zqq
 */
public class VariantUtil {

    public final static VariantMap EMPTY = new VariantMap() {
        public int getIndex(String name) {
            return -1;
        }

        public String getName(int index) {
            return null;
        }

        public int size() {
            return 0;
        }
    };

    public static VariantMap toVariantMap(Map<String, Integer> map) {
        if (map == null || map.isEmpty()) {
            return EMPTY;
        } else if (map.size() < 7) {
            return new ArrayVariantMap(map);
        } else {
            return new HashVariantMap(map);
        }
    }
}
