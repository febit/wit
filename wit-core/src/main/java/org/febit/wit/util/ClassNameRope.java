// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.util;

import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Accessors(fluent = true)
public class ClassNameRope {

    private final List<String> segments = new ArrayList<>(12);

    @Getter
    private int arrayDepth = 0;

    public ClassNameRope(String s) {
        this.segments.add(s);
    }

    public ClassNameRope append(String s) {
        segments.add(s);
        return this;
    }

    public ClassNameRope increaseArrayDepth() {
        arrayDepth++;
        return this;
    }

    public String build() {
        return segments.remove(segments.size() - 1);
    }

    public boolean isArray() {
        return arrayDepth > 0;
    }

    public boolean isSimpleName() {
        return segments.size() == 1;
    }

    public int size() {
        return segments.size();
    }

    public String simpleName() {
        if (segments.isEmpty()) {
            throw new IllegalStateException("Cannot happen, segments should not be empty");
        }
        return segments.get(segments.size() - 1);
    }

    public String componentName() {
        if (segments.isEmpty()) {
            throw new IllegalStateException("Cannot happen, segments should not be empty");
        }
        var buf = new StringBuilder();
        for (var s : segments) {
            buf.append(s).append('.');
        }
        return buf.substring(0, buf.length() - 1);
    }

    @Override
    public String toString() {
        var buf = new StringBuilder();
        for (int i = 0; i < segments.size(); i++) {
            if (i != 0) {
                buf.append('.');
            }
            buf.append(segments.get(i));
        }
        for (int i = 0; i < arrayDepth; i++) {
            buf.append('[').append(']');
        }
        return buf.toString();
    }
}
