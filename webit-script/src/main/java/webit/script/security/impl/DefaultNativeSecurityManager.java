// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.security.impl;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import webit.script.Engine;
import webit.script.Initable;
import webit.script.security.NativeSecurityManager;
import webit.script.util.StringUtil;

/**
 *
 * @author Zqq
 */
public class DefaultNativeSecurityManager implements NativeSecurityManager, Initable {

    private static final String ROOT_NODE_NAME = "*";

    private final ConcurrentMap<String, Node> NODES;
    //settings
    private String list;

    public DefaultNativeSecurityManager() {
        this.NODES = new ConcurrentHashMap<String, Node>();
        Node rootNode = new Node(null, ROOT_NODE_NAME);
        rootNode.setAccess(false);
        NODES.put(ROOT_NODE_NAME, rootNode);
    }

    public boolean access(String path) {
        return getNode(path).isAccess();
    }

    public void init(Engine engine) {

        for (String rule : StringUtil.toArray(list)) {
            char firstChar = rule.charAt(0);
            boolean access;
            if ((access = firstChar == '+') || firstChar == '-') {
                rule = rule.substring(1).trim();
            } else {
                access = true;
            }
            getNode(rule).setAccess(access);
        }
    }

    protected final Node getNode(final String name) {
        Node node;
        if ((node = NODES.get(name)) == null) {
            int index = name.lastIndexOf('.');
            node = new Node(getNode(index > 0 ? name.substring(0, index) : ROOT_NODE_NAME), name);
            Node old = NODES.putIfAbsent(name, node);
            if (old != null) {
                return old;
            }
        }
        return node;
    }

    public void setList(String list) {
        this.list = list;
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
            if (this.inherited) {
                return this.access;
            }
            this.inherited = true;
            return this.access = this.parent.isAccess();
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
