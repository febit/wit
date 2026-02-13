package org.febit.wit.runtime.heap;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.febit.wit.Context;
import org.febit.wit.exceptions.ScriptRuntimeException;
import org.febit.wit.runtime.FrameIndexer;
import org.jspecify.annotations.Nullable;

import java.util.function.BiConsumer;

/**
 * Variables heap.
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class VariantHeap implements Context.Heap {

    private final @Nullable Object[] table;
    /**
     * Upstream tables.
     * <p>
     * Functions are run in different page tables, to avoid conflicts.
     * <p>
     * Format: [page][index] => value
     */
    private final @Nullable Object[][] upstreams;
    /**
     * Variables indexers by frame.
     */
    private final FrameIndexer[] indexers;
    /**
     * Current frame.
     */
    private int frame;

    public VariantHeap(int size, FrameIndexer[] indexers) {
        this.indexers = indexers;
        this.frame = 0;
        this.table = new Object[size];
        this.upstreams = new Object[0][];
    }

    public static VariantHeap empty() {
        return new VariantHeap(0, new FrameIndexer[]{FrameIndexer.EMPTY});
    }

    public VariantHeap shift(int size, FrameIndexer[] indexers) {
        var up = this.upstreams;
        var pages = new Object[up.length + 1][];
        pages[0] = this.table;
        System.arraycopy(up, 0, pages, 1, up.length);
        return new VariantHeap(new Object[size], pages, indexers);
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
    public void set(String name, @Nullable Object value) {
        int idx = this.indexers[this.frame].lookupUpstream(name);
        if (idx >= 0) {
            this.table[idx] = value;
        }
    }

    @Nullable
    @Override
    public Object get(String name, boolean force) throws ScriptRuntimeException {
        int idx = currentIndexer().lookupUpstream(name);
        if (idx >= 0) {
            return this.table[idx];
        }
        if (force) {
            throw new ScriptRuntimeException("Not found variant named:" + name);
        }
        return null;
    }

    @Nullable
    public Object getFromUpstream(int page, int index) {
        return this.upstreams[page][index];
    }

    public void setUpstream(int page, int index, @Nullable Object value) {
        this.upstreams[page][index] = value;
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
