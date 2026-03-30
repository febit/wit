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
    static String[] split(@Nullable String path, char separator) {
        if (path == null || path.isEmpty()) {
            return new String[0];
        }
        int len = path.length();
        var list = new ArrayList<String>();
        int start = 0;
        for (int i = 1; i < len; i++) {
            if (path.charAt(i) != separator) {
                continue;
            }
            list.add(path.substring(start, i));
            start = i;
        }
        list.add(path.substring(start));
        return list.toArray(new String[0]);
    }

    /**
     * Matches the given path against the trie.
     *
     * @param path the path to match
     * @return true if allowed, false if denied or no matching rule
     */
    public boolean match(@Nullable String path) {
        var segments = split(path, this.separator);
        var node = this.root;
        for (var segment : segments) {
            var cadi = node.children.get(segment);
            if (cadi != null) {
                node = cadi;
                continue;
            }

            // Finished matching segments
            // Fallback if segment starts with separator
            if (!segment.isEmpty()
                    && segment.charAt(0) == this.separator
                    && node.fallbackChild != null
            ) {
                node = node.fallbackChild;
            }
            break;
        }
        return node.allowed != null && node.allowed;
    }

    public static final class Builder {
        private final char separator;
        private final String separatorString;
        private final List<Rule> rules = new ArrayList<>();
        private final NodeBuilder root = new NodeBuilder();

        private Builder(char separator) {
            this.separator = separator;
            this.separatorString = String.valueOf(separator);
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
            var segments = split(rule.path, this.separator);

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
            private final Map<String, NodeBuilder> children = new HashMap<>();
            @Nullable
            private Boolean allowed;

            Node build() {
                var frozen = new HashMap<String, Node>();
                for (var entry : this.children.entrySet()) {
                    frozen.put(entry.getKey(), entry.getValue().build());
                }
                return new Node(
                        Map.copyOf(frozen),
                        frozen.get(Builder.this.separatorString),
                        this.allowed
                );
            }
        }
    }

    private record Node(
            Map<String, Node> children,
            @Nullable Node fallbackChild,
            @Nullable Boolean allowed
    ) {
    }

}
