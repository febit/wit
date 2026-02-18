// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.util;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Accessors(fluent = true)
public class ClassNameRope {

    private final List<String> segment = new ArrayList<>(12);

    @Getter
    private int arrayDepth = 0;

    public ClassNameRope(String s) {
        this.segment.add(s);
    }

    public ClassNameRope append(String s) {
        segment.add(s);
        return this;
    }

    public ClassNameRope increaseArrayDepth() {
        arrayDepth++;
        return this;
    }

    public String pop() {
        return segment.remove(segment.size() - 1);
    }

    public boolean isArray() {
        return arrayDepth > 0;
    }

    public boolean isSimpleName() {
        return segment.size() == 1;
    }

    public int size() {
        return segment.size();
    }

    @Nullable
    public String simpleName() {
        if (segment.isEmpty()) {
            return null;
        }
        return segment.get(segment.size() - 1);
    }

    @Nullable
    public String componentName() {
        if (segment.isEmpty()) {
            return null;
        }
        var buf = new StringBuilder();
        for (var s : segment) {
            buf.append(s).append('.');
        }
        return buf.substring(0, buf.length() - 1);
    }

    @Override
    public String toString() {
        var buf = new StringBuilder();
        for (int i = 0; i < segment.size(); i++) {
            if (i != 0) {
                buf.append('.');
            }
            buf.append(segment.get(i));
        }
        for (int i = 0; i < arrayDepth; i++) {
            buf.append('[').append(']');
        }
        return buf.toString();
    }
}
