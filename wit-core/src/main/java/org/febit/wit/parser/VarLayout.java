// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.parser;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.febit.wit.Wit;
import org.febit.wit.exception.ParseException;
import org.febit.wit.runtime.ast.FrameIndexer;
import org.febit.wit.runtime.ast.Position;
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
     * All frames.
     */
    private final List<Frame> frames = new ArrayList<>();

    /**
     * Staked frame view, used to assign/locate vars.
     */
    private final Stack<Frame> frameView = new Stack<>();

    /**
     * Frame size stack of each layer, used to restore frame size when unshift layer.
     */
    private final Stack<Integer> frameSizeStack = new Stack<>();

    @Getter
    private final StaticHeaps staticHeaps;
    private final Frame root;

    @Getter
    private int frameSize;
    private int layerCursor;

    VarLayout(Wit wit) {
        this.staticHeaps = wit.staticHeaps();
        this.root = shiftFrame(-1);
        this.root.assignVarsIfAbsent(wit.predefinedVars());
    }

    private Frame shiftFrame(int upSeq) {
        var frame = new Frame(this.frames.size(), upSeq);
        this.frames.add(frame);
        this.frameView.push(frame);
        return frame;
    }

    public void shiftFrame() {
        shiftFrame(frameView.peek().seq);
    }

    public int unshiftFrame() {
        return frameView.pop().seq;
    }

    public void shiftLayer() {
        frameSizeStack.push(frameSize);
        frameSize = 0;
        layerCursor++;
        shiftFrame();
    }

    public void unshiftLayer() {
        frameSize = frameSizeStack.pop();
        layerCursor--;
        unshiftFrame();
    }

    public FrameIndexer[] buildFrameIndexers() {
        var size = this.frames.size();
        var result = new FrameIndexer[size];
        int i = 0;
        for (; i < size; i++) {
            if (this.frames.get(i).layerSeq == this.layerCursor) {
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
        return frameView.peek().assignVar(name, position);
    }

    public void assignConst(String name, @Nullable Object value, Position position) {
        frameView.peek().assignConst(name, value, position);
    }

    public VarAddress locate(String name, int frameOffset, boolean force, Position position) {

        //local var/const
        for (; frameOffset < frameView.size(); frameOffset++) {
            var address = frameView.peek(frameOffset).locate(name);
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
        return contextAddress(root.layerSeq, root.assignVar(name, position));
    }

    private VarAddress contextAddress(int layerSeq, int index) {
        return layerSeq == this.layerCursor
                ? VarAddress.ofVariable(index)
                : VarAddress.ofUpper(this.layerCursor - layerSeq - 1, index);
    }

    private static FrameIndexer createFrameIndexer(@Nullable FrameIndexer up, Map<String, Integer> map) {
        if (map.isEmpty()) {
            if (up != null) {
                return up;
            }
            return FrameIndexer.EMPTY;
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

        final int layerSeq;

        final Map<String, Integer> table = new HashMap<>(16);
        final Map<String, @Nullable Object> constMap = new HashMap<>(16);

        Frame(int seq, int upSeq) {
            this.seq = seq;
            this.upSeq = upSeq;
            this.layerSeq = VarLayout.this.layerCursor;
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
            return contextAddress(this.layerSeq, index);
        }

        void shouldNotAssigned(String name, Position position) {
            if (this.table.containsKey(name)) {
                throw new ParseException("Variable already exists: " + name, position);
            }
        }

        Integer assignVar(String name, Position position) {
            shouldNotAssigned(name, position);
            int index = VarLayout.this.frameSize++;
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
