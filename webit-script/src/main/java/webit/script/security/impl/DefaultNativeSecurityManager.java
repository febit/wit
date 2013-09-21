// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.security.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import webit.script.Initable;
import webit.script.Engine;
import webit.script.security.NativeSecurityManager;
import webit.script.util.StringUtil;

/**
 *
 * @author Zqq
 */
public class DefaultNativeSecurityManager implements NativeSecurityManager, Initable {

    //settings
    //private String listFiles;
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

        //do list
        if (list != null) {
            String[] nodeRules = StringUtil.splitc(list, DELIMITERS);
            StringUtil.trimAll(nodeRules);
            for (int i = 0; i < nodeRules.length; i++) {
                String rule = nodeRules[i];
                if (rule.length() > 0) {
                    char firstChar = rule.charAt(0);
                    String nodeName;
                    boolean access;
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

    private static Node getOrCreateNode(Map<String, Node> map, String name) {
        Node node = map.get(name);
        if (node == null) {
            Node parent = getOrCreateNode(map, getParentNodeName(name));
            node = new Node(parent, name);
            map.put(name, node);
        }
        return node;
    }

    protected final Node getOrCreateNode(String name) {
        Node node = allNodes.get(name);

        if (node == null) {
            Node parent = getOrCreateNode(getParentNodeName(name));
            node = new Node(parent, name);
            Node old = allNodes.putIfAbsent(name, node);
            if (old != null) {
                node = old;
            }
        }
        return node;
    }

    protected static String getParentNodeName(String name) {
        int index = name.lastIndexOf('.');
        return index > 0 ? name.substring(0, index) : ROOT_NODE_NAME;
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
                //already has a value
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
