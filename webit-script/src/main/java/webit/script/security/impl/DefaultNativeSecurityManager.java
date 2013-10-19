// Copyright (c) 2013, Webit Team. All Rights Reserved.
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

    //settings
    private String list;
    //
    private final static String ROOT_NODE_NAME = "";
    private final static char[] DELIMITERS = new char[]{'\n', ',', '\r'};
    private ConcurrentMap<String, Node> allNodes;

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

        if (list != null) {
            String[] nodeRules = StringUtil.splitc(list, DELIMITERS);
            StringUtil.trimAll(nodeRules);
            char firstChar;
            String nodeName;
            boolean access;
            String rule;
            for (int i = 0; i < nodeRules.length; i++) {
                rule = nodeRules[i];
                if (rule.length() > 0) {
                    firstChar = rule.charAt(0);
                    if (firstChar == '+' || firstChar == '-') {
                        access = firstChar == '+';
                        nodeName = rule.substring(1).trim();
                    } else {
                        access = true;
                        nodeName = rule;
                    }
                    getOrCreateNode(nodes, nodeName).setAccess(access);
                }
            }
        }
        allNodes = new ConcurrentHashMap<String, Node>(nodes);
    }

    protected final Node getOrCreateNode(final String name) {
        Node node;

        if ((node = allNodes.get(name)) == null) {
            node = new Node(
                    getOrCreateNode(getParentNodeName(name)),
                    name);
            Node old = allNodes.putIfAbsent(name, node);
            if (old != null) {
                node = old;
            }
        }
        return node;
    }

    private static Node getOrCreateNode(Map<String, Node> map, String name) {
        Node node;
        if ((node = map.get(name)) == null) {
            node = new Node(
                    getOrCreateNode(map, getParentNodeName(name)),
                    name);
            map.put(name, node);
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
            if (this.inherit == false) {
                //if already has a value
                //black list has higher priority
                if (access == false) {
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
