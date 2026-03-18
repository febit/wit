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
package org.febit.wit.util;

import lombok.experimental.UtilityClass;
import org.jspecify.annotations.Nullable;

@UtilityClass
public class Args {

    private static final Object[] EMPTY = new Object[0];

    public static Object[] empty() {
        return EMPTY;
    }

    @Nullable
    public static Object at(@Nullable Object @Nullable [] args, int i) {
        return args != null && i < args.length ? args[i] : null;
    }

    @SuppressWarnings({"unused"})
    public static @Nullable Object[] ensureSize(@Nullable Object @Nullable [] args, int size) {
        if (args == null) {
            return new Object[size];
        }
        if (args.length >= size) {
            return args;
        }
        var newArray = new Object[size];
        System.arraycopy(args, 0, newArray, 0, args.length);
        return newArray;
    }
}
