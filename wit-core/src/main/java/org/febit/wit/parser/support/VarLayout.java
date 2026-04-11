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
package org.febit.wit.parser.support;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.febit.wit.Wit;
import org.febit.wit.exception.ScriptParseException;
import org.febit.wit.ir.Position;
import org.febit.wit.ir.TextPosition;
import org.febit.wit.runtime.heap.GlobalHeaps;
import org.febit.wit.runtime.heap.ScopeTable;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.febit.wit.util.Defaults.nvl;

@Accessors(fluent = true)
public class VarLayout {

    /**
     * All scopes.
     */
    private final List<Scope> scopes = new ArrayList<>();

    /**
     * Stacked scope view, used to assign/locate vars.
     */
    private final Stack<Scope> scopeView = new Stack<>();

    /**
     * Slot size stack of each frame, used to restore slot size when unshift frame.
     */
    private final Stack<Integer> slotSizeStack = new Stack<>();

    @Getter
    private final GlobalHeaps globals;
    private final Scope root;

    @Getter
    private int slotSize;
    private int frameCursor;

    public VarLayout(Wit wit) {
        this.globals = wit.globals();
        this.root = shiftScope(-1);
        this.root.assignVarsIfAbsent(wit.predefinedVars());
    }

    private Scope shiftScope(int upSeq) {
        var scope = new Scope(this.scopes.size(), upSeq);
        this.scopes.add(scope);
        this.scopeView.push(scope);
        return scope;
    }

    public void shiftScope() {
        shiftScope(scopeView.peek().seq);
    }

    public int unshiftScope() {
        return scopeView.pop().seq;
    }

    public void shiftFrame() {
        slotSizeStack.push(slotSize);
        slotSize = 0;
        frameCursor++;
        shiftScope();
    }

    public void unshiftFrame() {
        slotSize = slotSizeStack.pop();
        frameCursor--;
        unshiftScope();
    }

    public List<ScopeTable> buildScopeTables() {
        var size = this.scopes.size();
        var tables = new ScopeTable[size];
        int i = 0;
        for (; i < size; i++) {
            if (this.scopes.get(i).frameSeq == this.frameCursor) {
                break;
            }
        }
        final int start = i;
        for (; i < size; i++) {
            var scope = this.scopes.get(i);
            // assert i == scope.seq
            // exclude const
            var slotMap = scope.table;
            scope.constMap.keySet().forEach(slotMap::remove);
            tables[i] = createScopeTable(scope.upSeq >= 0 ? tables[scope.upSeq] : null, slotMap);
        }
        return List.of(Arrays.copyOfRange(tables, start, size));
    }

    public int assignVar(String name, Position pos) {
        return scopeView.peek().assignVar(name, pos);
    }

    public void assignConst(String name, @Nullable Object value, Position pos) {
        scopeView.peek().assignConst(name, value, pos);
    }

    public VarAddress locate(String name, int scopeOffset, boolean force, Position pos) {

        //local var/const
        for (; scopeOffset < scopeView.size(); scopeOffset++) {
            var address = scopeView.peek(scopeOffset).locate(name);
            if (address != null) {
                return address;
            }
        }

        // static var/const
        if (globals.variables().has(name)) {
            return VarAddress.ofHeap(globals.variables(), name);
        }
        if (globals.constants().has(name)) {
            return VarAddress.ofDirect(globals.constants().get(name));
        }

        //failed
        if (force) {
            throw new ScriptParseException("No such variable: " + name, pos);
        }
        //assign at root
        return contextAddress(root.frameSeq, root.assignVar(name, pos));
    }

    private VarAddress contextAddress(int frameSeq, int slot) {
        return frameSeq == this.frameCursor
                ? VarAddress.ofVariable(slot)
                : VarAddress.ofUpper(this.frameCursor - frameSeq - 1, slot);
    }

    private static ScopeTable createScopeTable(@Nullable ScopeTable upper, Map<String, Integer> map) {
        if (map.isEmpty()) {
            return nvl(upper, ScopeTable.EMPTY);
        }

        int i = 0;
        var symbols = new ScopeTable.Symbol[map.size()];
        for (var e : map.entrySet()) {
            symbols[i++] = new ScopeTable.Symbol(e.getKey(), e.getValue());
        }
        return new ScopeTable(upper, symbols);
    }

    private class Scope {
        final int seq;
        final int upSeq;

        final int frameSeq;

        final Map<String, Integer> table = new HashMap<>(16);
        final Map<String, @Nullable Object> constMap = new HashMap<>(16);

        Scope(int seq, int upSeq) {
            this.seq = seq;
            this.upSeq = upSeq;
            this.frameSeq = VarLayout.this.frameCursor;
        }

        @Nullable
        VarAddress locate(String name) {
            var slot = this.table.get(name);
            if (slot == null) {
                return null;
            }
            if (slot < 0) {
                return VarAddress.ofDirect(this.constMap.get(name));
            }
            return contextAddress(this.frameSeq, slot);
        }

        void shouldNotAssigned(String name, Position position) {
            if (this.table.containsKey(name)) {
                throw new ScriptParseException("Variable already exists: " + name, position);
            }
        }

        Integer assignVar(String name, Position position) {
            shouldNotAssigned(name, position);
            int slot = VarLayout.this.slotSize++;
            this.table.put(name, slot);
            return slot;
        }

        void assignConst(final String name, @Nullable Object value, Position position) {
            shouldNotAssigned(name, position);
            this.table.put(name, -1);
            this.constMap.put(name, value);
        }

        void assignVarsIfAbsent(List<String> vars) {
            for (var v : vars) {
                if (!this.table.containsKey(v)) {
                    assignVar(v, TextPosition.UNKNOWN);
                }
            }
        }
    }

}
