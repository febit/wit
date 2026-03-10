// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.parser;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.febit.wit.Wit;
import org.febit.wit.exception.ParseException;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.ast.ScopedIndexer;
import org.febit.wit.runtime.ast.TextPosition;
import org.febit.wit.runtime.heap.StaticHeaps;
import org.febit.wit.util.Stack;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     * Heap size stack of each frame, used to restore heap size when unshift frame.
     */
    private final Stack<Integer> heapSizeStack = new Stack<>();

    @Getter
    private final StaticHeaps staticHeaps;
    private final Scope root;

    @Getter
    private int heapSize;
    private int frameCursor;

    VarLayout(Wit wit) {
        this.staticHeaps = wit.staticHeaps();
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
        heapSizeStack.push(heapSize);
        heapSize = 0;
        frameCursor++;
        shiftScope();
    }

    public void unshiftFrame() {
        heapSize = heapSizeStack.pop();
        frameCursor--;
        unshiftScope();
    }

    public List<ScopedIndexer> buildScopedIndexers() {
        var size = this.scopes.size();
        var result = new ScopedIndexer[size];
        int i = 0;
        for (; i < size; i++) {
            if (this.scopes.get(i).frameSeq == this.frameCursor) {
                break;
            }
        }
        final int start = i;
        for (; i < size; i++) {
            var scope = this.scopes.get(i);
            // assert i == stair.id
            // exclude const
            var indexerMap = scope.table;
            scope.constMap.keySet().forEach(indexerMap::remove);
            result[i] = createScopedIndexer(scope.upSeq >= 0 ? result[scope.upSeq] : null, indexerMap);
        }
        return List.of(Arrays.copyOfRange(result, start, size));
    }

    public int assignVar(String name, Position position) {
        return scopeView.peek().assignVar(name, position);
    }

    public void assignConst(String name, @Nullable Object value, Position position) {
        scopeView.peek().assignConst(name, value, position);
    }

    public VarAddress locate(String name, int scopeOffset, boolean force, Position position) {

        //local var/const
        for (; scopeOffset < scopeView.size(); scopeOffset++) {
            var address = scopeView.peek(scopeOffset).locate(name);
            if (address != null) {
                return address;
            }
        }

        // static var/const
        if (staticHeaps.variables().has(name)) {
            return VarAddress.ofHeap(staticHeaps.variables(), name);
        }
        if (staticHeaps.constants().has(name)) {
            return VarAddress.ofDirect(staticHeaps.constants().get(name));
        }

        //failed
        if (force) {
            throw new ParseException("No such variable: " + name, position);
        }
        //assign at root
        return contextAddress(root.frameSeq, root.assignVar(name, position));
    }

    private VarAddress contextAddress(int frameSeq, int index) {
        return frameSeq == this.frameCursor
                ? VarAddress.ofVariable(index)
                : VarAddress.ofUpper(this.frameCursor - frameSeq - 1, index);
    }

    private static ScopedIndexer createScopedIndexer(@Nullable ScopedIndexer up, Map<String, Integer> map) {
        if (map.isEmpty()) {
            if (up != null) {
                return up;
            }
            return ScopedIndexer.EMPTY;
        }

        int i = 0;
        var entries = new ScopedIndexer.Entry[map.size()];
        for (var entry : map.entrySet()) {
            entries[i++] = new ScopedIndexer.Entry(entry.getKey(), entry.getValue());
        }
        return new ScopedIndexer(up, entries);
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
            var index = this.table.get(name);
            if (index == null) {
                return null;
            }
            if (index < 0) {
                return VarAddress.ofDirect(this.constMap.get(name));
            }
            return contextAddress(this.frameSeq, index);
        }

        void shouldNotAssigned(String name, Position position) {
            if (this.table.containsKey(name)) {
                throw new ParseException("Variable already exists: " + name, position);
            }
        }

        Integer assignVar(String name, Position position) {
            shouldNotAssigned(name, position);
            int index = VarLayout.this.heapSize++;
            this.table.put(name, index);
            return index;
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
