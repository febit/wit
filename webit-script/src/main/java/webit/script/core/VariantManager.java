// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import webit.script.Engine;
import webit.script.exceptions.ParseException;
import webit.script.global.GlobalManager;
import webit.script.util.ArrayUtil;
import webit.script.util.Stack;

/**
 *
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
            VarStair stair = varStairs.get(i);
            if (stair.scopeLevel == this.scopeLevelCount) {
                break;
            }
        }
        final int start = i;
        for (; i < size; i++) {
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
        return Arrays.copyOfRange(result, start, size);
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
        return context(root.scopeLevel, root.assignVar(name, line, column));
    }

    private static VariantIndexer getVariantIndexer(final int id, final VariantIndexer parent, final Map<String, Integer> map) {
        if (map.isEmpty()) {
            if (parent != null) {
                return parent;
            }
            return new VariantIndexer(id, null, ArrayUtil.EMPTY_STRINGS, null);
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

    VarAddress context(int scope, int index) {
        if (scope == this.scopeLevelCount) {
            return new VarAddress(VarAddress.CONTEXT, index, null);
        } else {
            return new VarAddress(VarAddress.SCOPE, this.scopeLevelCount - scope - 1, index, null);
        }
    }

    static VarAddress global(int index) {
        return new VarAddress(VarAddress.GLOBAL, index, null);
    }

    static VarAddress constValue(Object value) {
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
                return constValue(this.constMap.get(name));
            }
            return context(this.scopeLevel, index);
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
                this.constMap = new HashMap<>();
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
