// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import webit.script.Engine;
import webit.script.exceptions.ParseException;
import webit.script.global.GlobalManager;
import webit.script.util.Stack;
import webit.script.util.StringUtil;

/**
 *
 * @author Zqq
 */
public class VariantManager {

    private int varCount;
    private final Stack<VarStair> stairStack;
    private final List<VarStair> stairs;
    private final VarStair root;
    private final GlobalManager globalManager;

    VariantManager(Engine engine) {
        this.globalManager = engine.getGlobalManager();
        this.stairs = new ArrayList<VarStair>();
        this.stairStack = new Stack<VarStair>();
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
        for (int i = 0; i < size; i++) {
            VarStair stair = varStairs.get(i);
            //assert i == stair.id
            //remove consts
            final Map<String, Integer> indexerMap = stair.values;
            if (stair.constMap != null) {
                for (String key : stair.constMap.keySet()) {
                    indexerMap.remove(key);
                }
            }
            result[i] = getVariantIndexer(i, stair.parentId >= 0 ? result[stair.parentId] : null, indexerMap);
        }
        return result;
    }

    public int assignVariant(String name, int line, int column) {
        return stairStack.peek().assignVar(name, line, column);
    }

    public void assignConst(String name, Object value, int line, int column) {
        stairStack.peek().assignConst(name, value, line, column);
    }

    public VarAddress locateAtUpstair(String name, int upstair, int line, int column) {
        VarAddress address = stairStack.peek(upstair).locate(name);
        if (address != null) {
            return address;
        }
        throw new ParseException("Can't locate vars: ".concat(name), line, column);
    }

    public VarAddress locate(String name, int fromUpstair, boolean force, int line, int column) {

        //local var/const
        for (; fromUpstair < stairStack.size(); fromUpstair++) {
            VarAddress address = stairStack.peek(fromUpstair).locate(name);
            if (address != null) {
                return address;
            }
        }

        //global var/const
        final GlobalManager globalMgr = this.globalManager;
        final int index = globalMgr.getGlobalIndex(name);
        if (index >= 0) {
            return global(index);
        }
        if (globalMgr.hasConst(name)) {
            return constValue(globalMgr.getConst(name));
        }

        //failed
        if (force) {
            throw new ParseException("Can't locate vars: ".concat(name), line, column);
        }
        //assign at root
        return context(root.assignVar(name, line, column));
    }

    private static VariantIndexer getVariantIndexer(final int id, final VariantIndexer parent, final Map<String, Integer> map) {
        if (map.isEmpty()) {
            if (parent != null) {
                return parent;
            }
            return new VariantIndexer(id, null, StringUtil.EMPTY_ARRAY, null);
        }
        final int size = map.size();
        final String[] names = new String[size];
        final int[] indexs = new int[size];
        int i = 0;
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            names[i] = entry.getKey();
            indexs[i] = entry.getValue();
            i++;
        }
        return new VariantIndexer(id, parent, names, indexs);
    }

    static VarAddress context(int index) {
        return new VarAddress(VarAddress.CONTEXT, index, null);
    }

    static VarAddress global(int index) {
        return new VarAddress(VarAddress.GLOBAL, index, null);
    }

    static VarAddress constValue(Object value) {
        return new VarAddress(VarAddress.CONST, -1, value);
    }

    private class VarStair {

        final int id;
        final int parentId;
        final Map<String, Integer> values;
        Map<String, Object> constMap;

        VarStair(int id, int parentId) {
            this.id = id;
            this.parentId = parentId;
            this.values = new HashMap<String, Integer>();
        }

        VarAddress locate(String name) {
            Integer index = this.values.get(name);
            if (index == null) {
                return null;
            }
            if (index < 0) {
                return constValue(this.constMap.get(name));
            }
            return context(index);
        }

        void checkDuplicate(final String name, int line, int column) {
            if (this.values.containsKey(name)) {
                throw new ParseException("Duplicate Variant declare: ".concat(name), line, column);
            }
        }

        Integer assignVar(final String name, int line, int column) {
            checkDuplicate(name, line, column);
            //XXX: rewrite
            int index = VariantManager.this.varCount++;
            this.values.put(name, index);
            return index;
        }

        void assignConst(final String name, final Object value, int line, int column) {
            checkDuplicate(name, line, column);
            if (this.constMap == null) {
                this.constMap = new HashMap<String, Object>();
            }
            this.values.put(name, -1);
            this.constMap.put(name, value);
        }

        void assignVarsIfAbsent(String[] vars) {
            if (vars == null) {
                return;
            }
            for (String var : vars) {
                if (!this.values.containsKey(var)) {
                    assignVar(var, -1, -1);
                }
            }
        }
    }

    public static class VarAddress {

        public static final int CONTEXT = 0;
        public static final int GLOBAL = 1;
        public static final int CONST = 2;

        public final int type;
        public final int index;
        public final Object constValue;

        VarAddress(int type, int index, Object constValue) {
            this.type = type;
            this.index = index;
            this.constValue = constValue;
        }
    }
}
