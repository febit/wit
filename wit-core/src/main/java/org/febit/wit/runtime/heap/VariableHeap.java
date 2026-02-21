package org.febit.wit.runtime.heap;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.febit.wit.exception.ScriptEvaluateException;
import org.febit.wit.runtime.ast.FrameIndexer;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.function.BiConsumer;

/**
 * Variables heap.
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class VariableHeap implements Heap {

    private final @Nullable Object[] table;
    /**
     * Upstream tables.
     * <p>
     * Functions are run in different layer tables, to avoid conflicts.
     * <p>
     * Format: [layer][index] => value
     */
    private final @Nullable Object[][] uppers;
    /**
     * Variables indexers by frame.
     */
    private final FrameIndexer[] indexers;
    /**
     * Current frame.
     */
    private int frame;

    public VariableHeap(int size, FrameIndexer[] indexers) {
        this.indexers = indexers;
        this.frame = 0;
        this.table = new Object[size];
        this.uppers = new Object[0][];
    }

    public static VariableHeap empty() {
        return new VariableHeap(0, new FrameIndexer[]{FrameIndexer.EMPTY});
    }

    public VariableHeap shift(int size, FrameIndexer[] indexers) {
        var up = this.uppers;
        var layers = new Object[up.length + 1][];
        layers[0] = this.table;
        System.arraycopy(up, 0, layers, 1, up.length);
        return new VariableHeap(new Object[size], layers, indexers);
    }

    public void onFrame(int frame, Runnable action) {
        var prev = this.frame;
        this.frame = frame;
        try {
            action.run();
        } finally {
            this.frame = prev;
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
        return currentIndexer().lookupUpstream(name) >= 0;
    }

    @Override
    public void set(String name, @Nullable Object value) {
        int idx = this.indexers[this.frame].lookupUpstream(name);
        if (idx >= 0) {
            this.table[idx] = value;
        }
    }

    @Nullable
    @Override
    public Object get(String name, boolean strict) throws ScriptEvaluateException {
        int idx = currentIndexer().lookupUpstream(name);
        if (idx >= 0) {
            return this.table[idx];
        }
        if (strict) {
            throw new ScriptEvaluateException("Not found variable named:" + name);
        }
        return null;
    }

    @Override
    public void clear() {
        Arrays.fill(this.table, null);
    }

    @Nullable
    public Object getAtLayer(int layer, int index) {
        return this.uppers[layer][index];
    }

    public void setAtLayer(int layer, int index, @Nullable Object value) {
        this.uppers[layer][index] = value;
    }

    public FrameIndexer currentIndexer() {
        return this.indexers[this.frame];
    }

    @Override
    public void each(BiConsumer<String, @Nullable Object> action) {
        var myVars = this.table;
        currentIndexer().each(
                (name, idx) -> action.accept(name, myVars[idx])
        );
    }

}
