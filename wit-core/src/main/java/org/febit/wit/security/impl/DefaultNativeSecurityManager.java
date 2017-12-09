// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.security.impl;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.febit.wit.Init;
import org.febit.wit.security.NativeSecurityManager;
import org.febit.wit.util.StringUtil;

/**
 *
 * @author zqq90
 */
public class DefaultNativeSecurityManager implements NativeSecurityManager {

    private static final String ROOT_NODE_NAME = "*";

    private final ConcurrentMap<String, Node> nodes;

    protected String list;

    public DefaultNativeSecurityManager() {
        this.nodes = new ConcurrentHashMap<>();
        Node rootNode = new Node(null, ROOT_NODE_NAME);
        rootNode.setAccess(false);
        this.nodes.put(ROOT_NODE_NAME, rootNode);
    }

    @Init
    public void init() {

        for (String rule : StringUtil.toArray(list)) {
            char firstChar = rule.charAt(0);
            boolean access = firstChar == '+';
            if (access || firstChar == '-') {
                rule = rule.substring(1).trim();
            } else {
                access = true;
            }
            getNode(rule).setAccess(access);
        }
    }

    @Override
    public boolean access(String path) {
        return getNode(path).isAccess();
    }

    public void setList(String list) {
        this.list = list;
    }

    protected final Node getNode(final String name) {
        Node node = this.nodes.get(name);
        if (node == null) {
            int index = name.lastIndexOf('.');
            node = new Node(getNode(index > 0 ? name.substring(0, index) : ROOT_NODE_NAME), name);
            Node old = this.nodes.putIfAbsent(name, node);
            if (old != null) {
                return old;
            }
        }
        return node;
    }

    protected static class Node {

        private boolean inherited;
        private boolean access;
        public final Node parent;
        public final String name;

        public Node(Node parent, String name) {
            this.parent = parent;
            this.name = name;
        }

        public final boolean isAccess() {
            if (!this.inherited) {
                this.access = this.parent.isAccess();
                this.inherited = true;
            }
            return this.access;
        }

        /**
         * set access.
         *
         * @param access
         * @return the value after set
         */
        public final boolean setAccess(boolean access) {
            if (this.inherited) {
                //if already has a value
                //black list has higher priority
                if (!access) {
                    this.access = false;
                    return false;
                }
                return this.access;
            }
            this.inherited = true;
            this.access = access;
            return access;
        }
    }
}
