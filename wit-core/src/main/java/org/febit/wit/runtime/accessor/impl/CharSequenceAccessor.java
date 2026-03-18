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
import org.jspecify.annotations.Nullable;

public class CharSequenceAccessor implements Getter<CharSequence> {

    @Nullable
    @Override
    public Object get(final CharSequence seq, @Nullable Object property) {
        if (property == null) {
            throw new ScriptEvaluateException("Property can't be null for CharSequence.");
        }
        if (property instanceof Number number) {
            try {
                return seq.charAt(number.intValue());
            } catch (IndexOutOfBoundsException e) {
                throw new ScriptEvaluateException("index out of bounds: " + number, e);
            }
        }
        return switch (property.toString()) {
            case "size", "length" -> seq.length();
            case "isEmpty" -> seq.isEmpty();
            default -> throw new ScriptEvaluateException(
                    "Invalid property or can't read: java.lang.CharSequence#" + property);
        };
    }
}
