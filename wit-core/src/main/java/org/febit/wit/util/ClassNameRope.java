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

    public String pop() {
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
