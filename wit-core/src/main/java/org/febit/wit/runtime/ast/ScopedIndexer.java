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
package org.febit.wit.runtime.ast;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;

import java.util.function.ObjIntConsumer;

@RequiredArgsConstructor
public final class ScopedIndexer {

    private static final int NONE = -1;

    public static final ScopedIndexer EMPTY = new ScopedIndexer(null, new Entry[0]);

    @Nullable
    private final ScopedIndexer upper;

    private final Entry[] entries;

    public record Entry(String name, int index) {
    }

    public int lookup(final String name) {
        for (var e : this.entries) {
            if (e.name.equals(name)) {
                return e.index;
            }
        }
        return NONE;
    }

    public int lookupWithUpper(final String name) {
        var index = lookup(name);
        if (index != NONE) {
            return index;
        }
        if (this.upper != null) {
            return upper.lookupWithUpper(name);
        }
        return NONE;
    }

    public void each(ObjIntConsumer<String> action) {
        for (var e : this.entries) {
            action.accept(e.name, e.index);
        }
    }

    public String name(int index) {
        return this.entries[index].name;
    }
}
