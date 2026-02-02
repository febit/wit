// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.accessor.impl;

import org.febit.wit.exception.ScriptEvaluateException;
import org.febit.wit.runtime.accessor.Getter;
import org.febit.wit.runtime.iter.Iter;
import org.jspecify.annotations.Nullable;

public class IterGetter implements Getter<Iter> {

    @Nullable
    @Override
    public Object get(Iter iter, @Nullable Object property) {
        if (property == null) {
            return null;
        }
        return switch (property.toString()) {
            case "hasNext" -> iter.hasNext();
            case "index" -> iter.index();
            case "isFirst" -> iter.index() == 0;
            case "next" -> iter.next();
            case "isEven" -> (iter.index() & 1) != 0;  // Note: index starts from 0
            case "isOdd" -> (iter.index() & 1) == 0;  // Note: index starts from 0
            default -> throw new ScriptEvaluateException(
                    "Invalid property or can't read: org.febit.wit.runtime.iter.Iter#" + property);
        };
    }
}
