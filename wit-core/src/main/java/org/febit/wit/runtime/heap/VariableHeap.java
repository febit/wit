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

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.febit.wit.exception.ScriptEvaluateException;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * Variables heap.
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class VariableHeap implements Heap {

    private final @Nullable Object[] slots;
    /**
     * Frame slots stack.
     * <p>
     * Functions are run in different frame slots, to avoid conflicts.
     * <p>
     * Format: [frame][slot] => variable value
     */
    private final @Nullable Object[][] frames;
    /**
     * Scope tables.
     */
    private final List<ScopeTable> scopeTables;
    /**
     * Current scope.
     */
    private int scope = 0;

    public VariableHeap(int slotSize, List<ScopeTable> scopeTables) {
        this.scopeTables = scopeTables;
        this.slots = new Object[slotSize];
        this.frames = new Object[0][];
    }

    public static VariableHeap empty() {
        return new VariableHeap(0, List.of(ScopeTable.EMPTY));
    }

    public VariableHeap pushFrame(int slotSize, List<ScopeTable> scopeTables) {
        var stack = this.frames;
        var next = new Object[stack.length + 1][];
        next[0] = this.slots;
        System.arraycopy(stack, 0, next, 1, stack.length);
        return new VariableHeap(new Object[slotSize], next, scopeTables);
    }

    public void withScope(int scope, Runnable action) {
        var prev = this.scope;
        this.scope = scope;
        try {
            action.run();
        } finally {
            this.scope = prev;
        }
    }

    public void set(int slot, @Nullable Object value) {
        this.slots[slot] = value;
    }

    public void set(
            int slot1, @Nullable Object value1,
            int slot2, @Nullable Object value2
    ) {
        var t = this.slots;
        t[slot1] = value1;
        t[slot2] = value2;
    }

    @Nullable
    public Object get(int slot) {
        return this.slots[slot];
    }

    @Override
    public boolean has(String name) {
        return table().findRecursive(name) >= 0;
    }

    @Override
    public void set(String name, @Nullable Object value) {
        int slot = this.scopeTables.get(this.scope).findRecursive(name);
        if (slot >= 0) {
            this.slots[slot] = value;
        }
    }

    @Nullable
    @Override
    public Object get(String name, boolean strict) throws ScriptEvaluateException {
        int slot = table().findRecursive(name);
        if (slot >= 0) {
            return this.slots[slot];
        }
        if (strict) {
            throw new ScriptEvaluateException("No such variable: " + name);
        }
        return null;
    }

    @Override
    public void clear() {
        Arrays.fill(this.slots, null);
    }

    @Nullable
    public Object getAtFrame(int frame, int slot) {
        return this.frames[frame][slot];
    }

    public void setAtFrame(int frame, int slot, @Nullable Object value) {
        this.frames[frame][slot] = value;
    }

    public ScopeTable table() {
        return this.scopeTables.get(this.scope);
    }

    @Override
    public void forEach(BiConsumer<String, @Nullable Object> action) {
        var thisSlots = this.slots;
        table().forEach(
                (name, slot) -> action.accept(name, thisSlots[slot])
        );
    }

}
