// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.core;

import org.febit.wit.Engine;
import org.febit.wit.exceptions.ParseException;
import org.febit.wit.global.GlobalManager;
import org.febit.wit.lang.Position;
import org.febit.wit.lang.TextPosition;
import org.febit.wit.lang.VariantIndexer;
import org.febit.wit.util.ArrayUtil;
import org.febit.wit.util.Stack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zqq90
 */
public class VariantManager {

    private int varCount;
    private int scopeLevelCount;
    private final Stack<Integer> varCountStack;
    private final Stack<VarStair> stairStack;
    private final List<VarStair> stairs;
    private final VarStair root;
    private final GlobalManager globalManager;

    VariantManager(Engine engine) {
        this.globalManager = engine.getGlobalManager();
        this.stairs = new ArrayList<>();
        this.stairStack = new Stack<>();
        this.varCountStack = new Stack<>();
        this.root = push(-1);
        this.root.assignVarsIfAbsent(engine.getVars());
    }

    private VarStair push(int parentId) {
        final VarStair stair = new VarStair(this.stairs.size(), parentId);
        this.stairs.add(stair);
        this.stairStack.push(stair);
        return stair;
    }

    public void push() {
        push(stairStack.peek().id);
    }

    public void pushScope() {
        scopeLevelCount++;
        varCountStack.push(varCount);
        varCount = 0;
        push();
    }

    public void popScope() {
        scopeLevelCount--;
        varCount = varCountStack.pop();
        pop();
    }

    public int getVarCount() {
        return varCount;
    }

    public int pop() {
        return stairStack.pop().id;
    }

    public VariantIndexer[] getIndexers() {
        final List<VarStair> varStairs = this.stairs;
        final int size = varStairs.size();
        final VariantIndexer[] result = new VariantIndexer[size];
        int i = 0;
        for (; i < size; i++) {
            if (varStairs.get(i).scopeLevel == this.scopeLevelCount) {
                break;
            }
        }
        final int start = i;
        for (; i < size; i++) {
            VarStair stair = varStairs.get(i);
            // assert i == stair.id
            // exclude const vars
            final Map<String, Integer> indexerMap = stair.values;
            if (stair.constMap != null) {
                stair.constMap.keySet().forEach(indexerMap::remove);
            }
            result[i] = getVariantIndexer(stair.parentId >= 0 ? result[stair.parentId] : null, indexerMap);
        }
        return Arrays.copyOfRange(result, start, size);
    }

    public int assignVariant(String name, Position position) {
        return stairStack.peek().assignVar(name, position);
    }

    public void assignConst(String name, Object value, Position position) {
        stairStack.peek().assignConst(name, value, position);
    }

    public VarAddress locateAtUpstair(String name, int upstair, Position position) {
        VarAddress address = stairStack.peek(upstair).locate(name);
        if (address != null) {
            return address;
        }
        throw new ParseException("Can't locate vars: ".concat(name), position);
    }

    public VarAddress locate(String name, int fromUpstair, boolean force, Position position) {

        //local var/const
        for (; fromUpstair < stairStack.size(); fromUpstair++) {
            VarAddress address = stairStack.peek(fromUpstair).locate(name);
            if (address != null) {
                return address;
            }
        }

        //global var/const
        final GlobalManager globalMgr = this.globalManager;
        if (globalMgr.hasGlobal(name)) {
            return globalAddress(name);
        }
        if (globalMgr.hasConst(name)) {
            return constAddress(globalMgr.getConst(name));
        }

        //failed
        if (force) {
            throw new ParseException("Can't locate vars: ".concat(name), position);
        }
        //assign at root
        return contextAddress(root.scopeLevel, root.assignVar(name, position));
    }

    private static VariantIndexer getVariantIndexer(final VariantIndexer parent, final Map<String, Integer> map) {
        if (map.isEmpty()) {
            if (parent != null) {
                return parent;
            }
            return new VariantIndexer(null, ArrayUtil.emptyStrings(), new int[0]);
        }
        final int size = map.size();
        final String[] names = new String[size];
        final int[] indexes = new int[size];
        int i = 0;
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            names[i] = entry.getKey();
            indexes[i] = entry.getValue();
            i++;
        }
        return new VariantIndexer(parent, names, indexes);
    }

    VarAddress contextAddress(int scope, int index) {
        return scope == this.scopeLevelCount
                ? new VarAddress(VarAddress.CONTEXT, index, null)
                : new VarAddress(VarAddress.SCOPE, this.scopeLevelCount - scope - 1, index, null);
    }

    VarAddress globalAddress(String name) {
        return new VarAddress(VarAddress.GLOBAL, -1, name);
    }

    VarAddress constAddress(Object value) {
        return new VarAddress(VarAddress.CONST, -1, value);
    }

    private class VarStair {

        final int scopeLevel;
        final int id;
        final int parentId;
        final Map<String, Integer> values;
        Map<String, Object> constMap;

        VarStair(int id, int parentId) {
            this.id = id;
            this.parentId = parentId;
            this.values = new HashMap<>();
            this.scopeLevel = VariantManager.this.scopeLevelCount;
        }

        VarAddress locate(String name) {
            Integer index = this.values.get(name);
            if (index == null) {
                return null;
            }
            if (index < 0) {
                return constAddress(this.constMap.get(name));
            }
            return contextAddress(this.scopeLevel, index);
        }

        void checkDuplicate(final String name, Position position) {
            if (this.values.containsKey(name)) {
                throw new ParseException("Duplicate Variant declare: ".concat(name), position);
            }
        }

        Integer assignVar(final String name, Position position) {
            checkDuplicate(name, position);
            //XXX: rewrite
            int index = VariantManager.this.varCount++;
            this.values.put(name, index);
            return index;
        }

        void assignConst(final String name, final Object value, Position position) {
            checkDuplicate(name, position);
            if (this.constMap == null) {
                this.constMap = new HashMap<>(16);
            }
            this.values.put(name, -1);
            this.constMap.put(name, value);
        }

        void assignVarsIfAbsent(String[] vars) {
            for (String var : vars) {
                if (!this.values.containsKey(var)) {
                    assignVar(var, TextPosition.UNKNOWN);
                }
            }
        }
    }

    public static class VarAddress {

        public static final int CONTEXT = 0;
        public static final int GLOBAL = 1;
        public static final int CONST = 2;
        public static final int SCOPE = 4;

        public final int type;
        public final int index;
        public final int scopeOffset;
        public final Object constValue;

        VarAddress(int type, int offset, int index, Object constValue) {
            this.type = type;
            this.scopeOffset = offset;
            this.index = index;
            this.constValue = constValue;
        }

        VarAddress(int type, int index, Object constValue) {
            this(type, 0, index, constValue);
        }
    }
}
