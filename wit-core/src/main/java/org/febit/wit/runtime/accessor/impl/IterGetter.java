/*
 * Copyright 2013-present febit.org (support@febit.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
