package org.febit.wit.runtime.heap;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.febit.wit.exception.ScriptEvaluateException;
import org.febit.wit.runtime.ast.ScopedIndexer;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * Variables heap.
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class VariableHeap implements Heap {

    private final @Nullable Object[] table;
    /**
     * Upper frames of variables.
     * <p>
     * Functions are run in different frame tables, to avoid conflicts.
     * <p>
     * Format: [frame][index] => value
     */
    private final @Nullable Object[][] uppers;
    /**
     * Indexers for each scope.
     */
    private final List<ScopedIndexer> indexers;
    /**
     * Current scope.
     */
    private int scope;

    public VariableHeap(int size, List<ScopedIndexer> indexers) {
        this.indexers = indexers;
        this.scope = 0;
        this.table = new Object[size];
        this.uppers = new Object[0][];
    }

    public static VariableHeap empty() {
        return new VariableHeap(0, List.of(ScopedIndexer.EMPTY));
    }

    public VariableHeap shift(int size, List<ScopedIndexer> indexers) {
        var up = this.uppers;
        var frames = new Object[up.length + 1][];
        frames[0] = this.table;
        System.arraycopy(up, 0, frames, 1, up.length);
        return new VariableHeap(new Object[size], frames, indexers);
    }

    public void onScope(int scope, Runnable action) {
        var prev = this.scope;
        this.scope = scope;
        try {
            action.run();
        } finally {
            this.scope = prev;
        }
    }

    public void set(int idx, @Nullable Object value) {
        this.table[idx] = value;
    }

    public void set(
            int idx1, @Nullable Object value1,
            int idx2, @Nullable Object value2
    ) {
        var t = this.table;
        t[idx1] = value1;
        t[idx2] = value2;
    }

    @Nullable
    public Object get(int idx) {
        return this.table[idx];
    }

    @Override
    public boolean has(String name) {
        return indexer().lookupWithUpper(name) >= 0;
    }

    @Override
    public void set(String name, @Nullable Object value) {
        int idx = this.indexers.get(this.scope).lookupWithUpper(name);
        if (idx >= 0) {
            this.table[idx] = value;
        }
    }

    @Nullable
    @Override
    public Object get(String name, boolean strict) throws ScriptEvaluateException {
        int idx = indexer().lookupWithUpper(name);
        if (idx >= 0) {
            return this.table[idx];
        }
        if (strict) {
            throw new ScriptEvaluateException("No such variable: " + name);
        }
        return null;
    }

    @Override
    public void clear() {
        Arrays.fill(this.table, null);
    }

    @Nullable
    public Object getAtFrame(int frame, int index) {
        return this.uppers[frame][index];
    }

    public void setAtFrame(int frame, int index, @Nullable Object value) {
        this.uppers[frame][index] = value;
    }

    public ScopedIndexer indexer() {
        return this.indexers.get(this.scope);
    }

    @Override
    public void each(BiConsumer<String, @Nullable Object> action) {
        var myVars = this.table;
        indexer().each(
                (name, idx) -> action.accept(name, myVars[idx])
        );
    }

}
