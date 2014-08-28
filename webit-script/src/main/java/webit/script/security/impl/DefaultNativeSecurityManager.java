// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.security.impl;

import java.util.HashMap;
import java.util.Map;
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
    private ConcurrentMap<String, Node> allNodes;
    //settings
    private String list;

    public boolean access(String path) {
        return getOrCreateNode(path).isAccess();
    }

    public void setList(String list) {
        this.list = list;
    }

    public void init(Engine engine) {
        Map<String, Node> nodes = new HashMap<String, Node>();

        Node rootNode = new Node(null, ROOT_NODE_NAME);
        rootNode.setAccess(false);
        nodes.put(ROOT_NODE_NAME, rootNode);

        for (String rule : StringUtil.splitAndRemoveBlank(list)) {
            char firstChar = rule.charAt(0);
            boolean access;
            if ((access = firstChar == '+') || firstChar == '-') {
                rule = rule.substring(1).trim();
            } else {
                access = true;
            }
            getOrCreateNode(nodes, rule).setAccess(access);
        }
        allNodes = new ConcurrentHashMap<String, Node>(nodes);
    }

    protected final Node getOrCreateNode(final String name) {
        Node node;
        if ((node = allNodes.get(name)) == null) {
            Node old;
            if ((old = allNodes.putIfAbsent(name,
                    node = new Node(getOrCreateNode(getParentNodeName(name)), name)))
                    != null) {
                return old;
            }
        }
        return node;
    }

    private static Node getOrCreateNode(Map<String, Node> map, String name) {
        Node node;
        if ((node = map.get(name)) == null) {
            map.put(name,
                    node = new Node(getOrCreateNode(map, getParentNodeName(name)), name));
        }
        return node;
    }

    private static String getParentNodeName(final String name) {
        int index;
        return (index = name.lastIndexOf('.')) > 0 ? name.substring(0, index) : ROOT_NODE_NAME;
    }

    protected static class Node {

        private boolean inherit;
        private boolean access;
        private final Node parent;
        private final String name;

        public Node(Node parent, String name) {
            this.parent = parent;
            this.name = name;
            this.inherit = true;
            this.access = false;
        }

        public final boolean isAccess() {
            if (inherit) {
                access = parent.isAccess();
                inherit = false;
            }
            return access;
        }

        /**
         *
         * @param access
         * @return the value after set
         */
        public final boolean setAccess(boolean access) {
            if (!this.inherit) {
                //if already has a value
                //black list has higher priority
                if (!access) {
                    this.access = false;
                    return false;
                } else {
                    return this.access;
                }
            } else {
                this.inherit = false;
                this.access = access;
                return access;
            }
        }

        public final Node getParent() {
            return parent;
        }

        public final String getName() {
            return name;
        }
    }
}
