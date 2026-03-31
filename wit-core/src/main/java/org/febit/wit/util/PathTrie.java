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

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Immutable prefix tree (trie) for path matching with custom separators.
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class PathTrie {

    private static final Segment[] EMPTY_SEGMENTS = {};

    private final char separator;
    private final Node root;

    public static Builder builder(char separator) {
        return new Builder(separator);
    }

    /**
     * Split path into segments. Empty path returns empty array.
     * <pre>
     * Empty/NULL   -> []
     * /            -> ["/"]
     * //            -> ["/", "/"]
     * /a           -> ["/a"]
     * /a/b         -> ["/a", "/b"]
     * /a/          -> ["/a", "/"]
     * /a/b/        -> ["/a", "/b", "/"]
     * a            -> ["a"]
     * a/b          -> ["a", "/b"]
     * </pre>
     */
    static Segment[] segments(@Nullable String path, char separator) {
        if (path == null || path.isEmpty()) {
            return EMPTY_SEGMENTS;
        }
        int len = path.length();

        // Pre-count segments to allocate exact-sized array
        int count = 1;
        for (int i = 1; i < len; i++) {
            if (path.charAt(i) == separator) {
                count++;
            }
        }

        var segments = new Segment[count];
        int start = 0;
        int idx = 0;
        for (int i = 1; i < len; i++) {
            if (path.charAt(i) != separator) {
                continue;
            }
            segments[idx++] = new Segment(path, start, i);
            start = i;
        }
        segments[idx] = new Segment(path, start, len);
        return segments;
    }

    /**
     * Matches the given path against the trie.
     *
     * @param path the path to match
     * @return true if allowed, false if denied or no matching rule
     */
    public boolean match(@Nullable String path) {
        if (path == null || path.isEmpty()) {
            return this.root.allowed != null && this.root.allowed;
        }

        int len = path.length();
        var node = this.root;
        int segStart = 0;
        // Reusable segment for HashMap lookups — avoids per-segment allocation
        var lookup = new Segment(path, 0, 0);

        for (int i = 1; ; i++) {
            if (i < len && path.charAt(i) != this.separator) {
                continue;
            }

            // Found segment boundary [segStart, i)
            lookup.reset(segStart, i);

            var found = node.children.get(lookup);
            if (found != null) {
                node = found;
                segStart = i;
                if (i >= len) {
                    break;
                }
                continue;
            }

            // No child match — check fallback if segment starts with separator
            if (segStart != i
                    && path.charAt(segStart) == this.separator
                    && node.fallbackChild != null
            ) {
                node = node.fallbackChild;
            }
            break;
        }
        return node.allowed != null && node.allowed;
    }

    private record Node(
            Map<Segment, Node> children,
            @Nullable Node fallbackChild,
            @Nullable Boolean allowed
    ) {
    }

    public static final class Builder {
        private final char separator;
        private final Segment fallbackKey;
        private final List<Rule> rules = new ArrayList<>();
        private final NodeBuilder root = new NodeBuilder();

        private Builder(char separator) {
            this.separator = separator;
            this.fallbackKey = new Segment(String.valueOf(separator), 0, 1);
        }

        public Builder allow(String path) {
            return rule(path, true);
        }

        public Builder deny(String path) {
            return rule(path, false);
        }

        public Builder rule(String path, boolean allowed) {
            this.rules.add(new Rule(path, allowed));
            return this;
        }

        public PathTrie build() {
            for (var rule : this.rules) {
                insert(rule);
            }
            return new PathTrie(this.separator, this.root.build());
        }

        private void insert(Rule rule) {
            var node = this.root;
            var segments = segments(rule.path, this.separator);

            for (var segment : segments) {
                node = node.children.computeIfAbsent(segment, k -> new NodeBuilder());
            }
            node.allowed = merge(node.allowed, rule.allowed);
        }

        private static Boolean merge(@Nullable Boolean current, boolean allowed) {
            if (current == null) {
                return allowed;
            }
            // Deny takes precedence
            return allowed && current;
        }

        private record Rule(String path, boolean allowed) {
        }

        private class NodeBuilder {
            private final Map<Segment, NodeBuilder> children = new HashMap<>();
            @Nullable
            private Boolean allowed;

            Node build() {
                var frozen = new HashMap<Segment, Node>();
                for (var entry : this.children.entrySet()) {
                    frozen.put(entry.getKey().compact(), entry.getValue().build());
                }
                return new Node(
                        Map.copyOf(frozen),
                        frozen.get(Builder.this.fallbackKey),
                        this.allowed
                );
            }
        }
    }

    static class Segment {

        private final String base;
        private int start;
        private int end;

        private int hash = 0;

        Segment(String base, int start, int end) {
            this.base = base;
            this.start = start;
            this.end = end;
        }

        /**
         * Resets start/end for reuse as a HashMap lookup key.
         */
        void reset(int start, int end) {
            this.start = start;
            this.end = end;
            this.hash = 0;
        }

        Segment compact() {
            if (start == 0 && end == base.length()) {
                return this;
            }
            return new Segment(toString(), 0, end - start);
        }

        @Override
        public String toString() {
            return base.substring(start, end);
        }

        @Override
        public int hashCode() {
            int h = this.hash;
            if (h != 0) {
                return h;
            }
            for (int i = start; i < end; i++) {
                h = 31 * h + base.charAt(i);
            }
            if (h == 0) {
                h = 1;
            }
            this.hash = h;
            return h;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof Segment other)) {
                return false;
            }
            if (end - start != other.end - other.start) {
                return false;
            }
            if (hash != 0 && other.hash != 0 && hash != other.hash) return false;
            return base.regionMatches(start, other.base, other.start, end - start);
        }
    }
}
