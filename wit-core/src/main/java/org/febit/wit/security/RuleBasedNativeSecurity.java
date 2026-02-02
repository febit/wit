// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.security;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class RuleBasedNativeSecurity implements NativeSecurity {

    static final String ROOT_PATH = "*";

    private final ConcurrentMap<String, Node> nodes;

    public static Builder builder() {
        return new Builder();
    }

    private static Node node(Map<String, Node> nodes, String path) {
        Node node = nodes.get(path);
        if (node != null) {
            return node;
        }

        int index = path.lastIndexOf('.');
        node = new Node(node(nodes, index > 0 ? path.substring(0, index) : ROOT_PATH), path);

        Node existed = nodes.putIfAbsent(path, node);
        if (existed != null) {
            return existed;
        }
        return node;
    }

    @Override
    public boolean allowed(String path) {
        return node(this.nodes, path).allowed();
    }

    public static class Builder {

        private boolean rootAllowed = false;
        private final List<Rule> rules = new ArrayList<>();

        public Builder allowRoot() {
            this.rootAllowed = true;
            return this;
        }

        public Builder denyRoot() {
            this.rootAllowed = false;
            return this;
        }

        public Builder allow(String path) {
            return rule(path, true);
        }

        public Builder allow(String... paths) {
            for (var p : paths) {
                rule(p, true);
            }
            return this;
        }

        public Builder allow(Collection<String> paths) {
            for (var p : paths) {
                rule(p, true);
            }
            return this;
        }

        public Builder deny(String path) {
            return rule(path, false);
        }

        public Builder deny(String... paths) {
            for (var p : paths) {
                rule(p, false);
            }
            return this;
        }

        public Builder deny(Collection<String> paths) {
            for (var p : paths) {
                rule(p, false);
            }
            return this;
        }

        public Builder rule(String path, boolean allowed) {
            this.rules.add(new Rule(path, allowed));
            return this;
        }

        public RuleBasedNativeSecurity build() {
            var nodes = new HashMap<String, Node>();
            var root = new Node(null, ROOT_PATH);
            root.allowed(rootAllowed);
            nodes.put(ROOT_PATH, root);

            for (var rule : rules) {
                node(nodes, rule.path()).allowed(rule.allowed());
            }
            return new RuleBasedNativeSecurity(new ConcurrentHashMap<>(nodes));
        }

        private record Rule(String path, boolean allowed) {
        }
    }

    @Getter
    @Accessors(fluent = true)
    private static class Node {
        @Nullable
        public final Node parent;
        public final String name;

        private boolean inherited;
        private boolean allowed;

        public Node(@Nullable Node parent, String name) {
            this.parent = parent;
            this.name = name;
        }

        public final boolean allowed() {
            if (!this.inherited) {
                if (this.parent == null) {
                    throw new IllegalStateException(
                            "No parent for non-inherited node: " + this.name);
                }
                this.allowed = this.parent.allowed();
                this.inherited = true;
            }
            return this.allowed;
        }

        public final void allowed(boolean allowed) {
            if (this.inherited) {
                //if already has a value
                //black list has higher priority
                if (!allowed) {
                    this.allowed = false;
                }
                return;
            }
            this.inherited = true;
            this.allowed = allowed;
        }
    }
}
