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
package org.febit.wit.runtime.heap;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;

import java.util.function.ObjIntConsumer;

@RequiredArgsConstructor
public class ScopeTable {

    private static final int NOT_FOUND = -1;

    public static final ScopeTable EMPTY = new ScopeTable(null, new Symbol[0]);

    @Nullable
    private final ScopeTable upper;

    private final Symbol[] symbols;

    public record Symbol(String name, int slot) {
    }

    public int find(final String name) {
        for (var symbol : this.symbols) {
            if (symbol.name.equals(name)) {
                return symbol.slot;
            }
        }
        return NOT_FOUND;
    }

    public int findRecursive(final String name) {
        var slot = find(name);
        if (slot != NOT_FOUND) {
            return slot;
        }
        if (this.upper != null) {
            return upper.findRecursive(name);
        }
        return NOT_FOUND;
    }

    public void forEach(ObjIntConsumer<String> action) {
        for (var symbol : this.symbols) {
            action.accept(symbol.name, symbol.slot);
        }
    }

    public String nameOf(int slot) {
        return this.symbols[slot].name;
    }
}
