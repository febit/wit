// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.core;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.febit.wit.Engine;
import org.febit.wit.exceptions.ParseException;
import org.febit.wit.runtime.FrameIndexer;
import org.febit.wit.runtime.Position;
import org.febit.wit.runtime.TextPosition;
import org.febit.wit.runtime.heap.StaticHeaps;
import org.febit.wit.util.ArrayUtils;
import org.febit.wit.util.Stack;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Accessors(fluent = true)
public class VariantManager {

    private final List<Frame> frames = new ArrayList<>();
    private final Stack<Frame> frameStack = new Stack<>();
    private final Stack<Integer> pageVarCounterStack = new Stack<>();

    private final StaticHeaps staticHeaps;
    private final Frame root;

    @Getter
    private int varCounter;
    private int pageCounter;

    VariantManager(Engine engine) {
        this.staticHeaps = engine.staticHeaps();
        this.root = shiftFrame(-1);
        this.root.assignVarsIfAbsent(engine.predefinedVars());
    }

    private Frame shiftFrame(int upSeq) {
        var frame = new Frame(this.frames.size(), upSeq);
        this.frames.add(frame);
        this.frameStack.push(frame);
        return frame;
    }

    public void shiftFrame() {
        shiftFrame(frameStack.peek().seq);
    }

    public int unshiftFrame() {
        return frameStack.pop().seq;
    }

    public void shiftPage() {
        pageVarCounterStack.push(varCounter);
        varCounter = 0;
        pageCounter++;
        shiftFrame();
    }

    public void unshiftPage() {
        varCounter = pageVarCounterStack.pop();
        pageCounter--;
        unshiftFrame();
    }

    public FrameIndexer[] constructIndexers() {
        var size = this.frames.size();
        var result = new FrameIndexer[size];
        int i = 0;
        for (; i < size; i++) {
            if (this.frames.get(i).pageSeq == this.pageCounter) {
                break;
            }
        }
        final int start = i;
        for (; i < size; i++) {
            var frame = this.frames.get(i);
            // assert i == stair.id
            // exclude const
            var indexerMap = frame.table;
            frame.constMap.keySet().forEach(indexerMap::remove);
            result[i] = createFrameIndexer(frame.upSeq >= 0 ? result[frame.upSeq] : null, indexerMap);
        }
        return Arrays.copyOfRange(result, start, size);
    }

    public int assignVar(String name, Position position) {
        return frameStack.peek().assignVar(name, position);
    }

    public void assignConst(String name, @Nullable Object value, Position position) {
        frameStack.peek().assignConst(name, value, position);
    }

    public VarAddress locate(String name, int frameOffset, boolean force, Position position) {

        //local var/const
        for (; frameOffset < frameStack.size(); frameOffset++) {
            var address = frameStack.peek(frameOffset).locate(name);
            if (address != null) {
                return address;
            }
        }

        // static var/const
        if (staticHeaps.variant().has(name)) {
            return VarAddress.ofStaticVar(name);
        }
        if (staticHeaps.constant().has(name)) {
            return VarAddress.ofConst(staticHeaps.constant().get(name));
        }

        //failed
        if (force) {
            throw new ParseException("Variant not found: " + name, position);
        }
        //assign at root
        return contextAddress(root.pageSeq, root.assignVar(name, position));
    }

    private VarAddress contextAddress(int pageId, int index) {
        return pageId == this.pageCounter
                ? VarAddress.ofContext(index)
                : VarAddress.ofUpstream(this.pageCounter - pageId - 1, index);
    }

    private static FrameIndexer createFrameIndexer(@Nullable FrameIndexer up, Map<String, Integer> map) {
        if (map.isEmpty()) {
            if (up != null) {
                return up;
            }
            return new FrameIndexer(null, ArrayUtils.emptyStrings(), new int[0]);
        }
        var size = map.size();
        var names = new String[size];
        var indexes = new int[size];
        int i = 0;
        for (var entry : map.entrySet()) {
            names[i] = entry.getKey();
            indexes[i] = entry.getValue();
            i++;
        }
        return new FrameIndexer(up, names, indexes);
    }

    private class Frame {
        final int seq;
        final int upSeq;

        final int pageSeq;

        final Map<String, Integer> table = new HashMap<>(16);
        final Map<String, @Nullable Object> constMap = new HashMap<>(16);

        Frame(int seq, int upSeq) {
            this.seq = seq;
            this.upSeq = upSeq;
            this.pageSeq = VariantManager.this.pageCounter;
        }

        @Nullable
        VarAddress locate(String name) {
            var index = this.table.get(name);
            if (index == null) {
                return null;
            }
            if (index < 0) {
                return VarAddress.ofConst(this.constMap.get(name));
            }
            return contextAddress(this.pageSeq, index);
        }

        void shouldNotAssigned(String name, Position position) {
            if (this.table.containsKey(name)) {
                throw new ParseException("Variant already exists: " + name, position);
            }
        }

        Integer assignVar(String name, Position position) {
            shouldNotAssigned(name, position);
            int index = VariantManager.this.varCounter++;
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
