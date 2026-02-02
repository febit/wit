// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.util;

import lombok.experimental.UtilityClass;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

@UtilityClass
public class CollectionUtils {

    public static int size(@Nullable Object object) {
        if (object == null) {
            return 0;
        }
        if (object.getClass().isArray()) {
            return Array.getLength(object);
        }
        if (object instanceof Collection<?> collection) {
            return collection.size();
        }
        if (object instanceof Map<?, ?> map) {
            return map.size();
        }
        if (object instanceof CharSequence cs) {
            return cs.length();
        }
        return -1;
    }

}
