// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zqq90
 */
public class ClassNameBand {

    private final List<String> segment;
    private int arrayDepth;

    public ClassNameBand(String s) {
        this.arrayDepth = 0;
        this.segment = new ArrayList<>(12);
        this.segment.add(s);
    }

    public ClassNameBand append(String s) {
        segment.add(s);
        return this;
    }

    public String pop() {
        return segment.remove(segment.size() - 1);
    }

    public int getArrayDepth() {
        return arrayDepth;
    }

    public ClassNameBand plusArrayDepth() {
        arrayDepth++;
        return this;
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

    public String getClassSimpleName() {
        if (segment.isEmpty()) {
            return null;
        }
        return segment.get(segment.size() - 1);
    }

    public String getClassPureName() {
        if (segment.isEmpty()) {
            return null;
        }
        return StringUtil.join(segment, '.');
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(StringUtil.join(segment, '.'));
        for (int i = 0; i < arrayDepth; i++) {
            buf.append('[').append(']');
        }
        return buf.toString();
    }
}
