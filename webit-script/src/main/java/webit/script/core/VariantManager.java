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
    private final Stack<IndexerRaw> rawStack;
    private final List<IndexerRaw> raws;
    private final IndexerRaw root;
    private final GlobalManager globalManager;

    @SuppressWarnings("unchecked")
    VariantManager(Engine engine) {
        this.globalManager = engine.getGlobalManager();
        this.raws = new ArrayList<IndexerRaw>();
        this.rawStack = new Stack<IndexerRaw>();
        this.root = push(-1);
        final String[] vars = engine.getVars();
        if (vars != null) {
            for (String var : vars) {
                if (!root.contains(var)) {
                    root.assignVar(var, -1, -1);
                }
                //ignore duplicate
            }
        }
    }

    private IndexerRaw push(int parentId) {
        IndexerRaw raw = new IndexerRaw(this.raws.size(), parentId);
        this.raws.add(raw);
        this.rawStack.push(raw);
        return raw;
    }

    public void push() {
        push(rawStack.peek().id);
    }

    public int getVarCount() {
        return varCount;
    }

    public int pop() {
        return rawStack.pop().id;
    }

    public VariantIndexer[] getIndexers() {
        List<IndexerRaw> raws = this.raws;
        final int size = raws.size();
        final VariantIndexer[] result = new VariantIndexer[size];
        for (int i = 0; i < size; i++) {
            IndexerRaw raw = raws.get(i);
            //assert i == raw.id
            //remove consts
            final Map<String, Integer> indexerMap = raw.values;
            if (raw.constMap != null) {
                for (String key : raw.constMap.keySet()) {
                    indexerMap.remove(key);
                }
            }
            result[i] = getVariantIndexer(i, raw.parentId >= 0 ? result[raw.parentId] : null, indexerMap);
        }
        return result;
    }

    private static VariantIndexer getVariantIndexer(final int id, final VariantIndexer parent, final Map<String, Integer> map) {
        if (map == null || map.isEmpty()) {
            if (parent != null) {
                return parent;
            } else {
                return new VariantIndexer(id, null, StringUtil.EMPTY_ARRAY, null);
            }
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

    public VarAddress assignVariantAddress(String name, int line, int column) {
        return context(assignVariant(name, line, column));
    }

    public int assignVariant(String name, int line, int column) {
        return rawStack.peek().assignVar(name, line, column);
    }

    public void assignConst(String name, Object value, int line, int column) {
        rawStack.peek().assignConst(name, value, line, column);
    }

    public VarAddress assignVariantAtRoot(final String name, int line, int column) {
        root.checkDuplicate(name, line, column);
        final int address = root.assignVar(name, line, column);
        return context(address);
    }

    public VarAddress locateAtUpstair(String name, int upstair, int line, int column) {
        try {
            IndexerRaw raw = rawStack.peek(upstair);
            Integer index;
            if ((index = raw.getIndex(name)) != null) {
                if (index >= 0) {
                    return context(index);
                } else {
                    return constValue(raw.getConstValue(name));
                }
            }
            throw new ParseException("Can't locate vars: ".concat(name), line, column);
        } catch (IndexOutOfBoundsException e) {
            throw new ParseException(StringUtil.format("Stack overflow when locate vars '{}' at upstair {}", name, upstair), line, column);
        }
    }

    public VarAddress locate(String name, int fromUpstair, boolean force, int line, int column) {

        //local var/const
        for (; fromUpstair < rawStack.size(); fromUpstair++) {
            IndexerRaw raw = rawStack.peek(fromUpstair);
            Integer index;
            if ((index = raw.getIndex(name)) != null) {
                if (index >= 0) {
                    return context(index);
                } else {
                    return constValue(raw.getConstValue(name));
                }
            }
        }

        //global var/const
        final GlobalManager globalManager = this.globalManager;
        final int index = globalManager.getGlobalIndex(name);
        if (index >= 0) {
            return global(index);
        }
        if (globalManager.hasConst(name)) {
            return constValue(globalManager.getConst(name));
        }

        //failed
        if (force) {
            throw new ParseException("Can't locate vars: ".concat(name), line, column);
        }
        return assignVariantAtRoot(name, line, column);
    }

    private static VarAddress context(int index) {
        return new VarAddress(VarAddress.CONTEXT, index, null);
    }

    private static VarAddress global(int index) {
        return new VarAddress(VarAddress.GLOBAL, index, null);
    }

    private static VarAddress constValue(Object value) {
        return new VarAddress(VarAddress.CONST, -1, value);
    }

    private class IndexerRaw {

        final int id;
        final int parentId;
        final Map<String, Integer> values;
        Map<String, Object> constMap;

        IndexerRaw(int id, int parentId) {
            this.parentId = parentId;
            this.values = new HashMap<String, Integer>();
            this.id = id;
        }

        Integer getIndex(String name) {
            return this.values.get(name);
        }

        Object getConstValue(String name) {
            return this.constMap != null
                    ? this.constMap.get(name)
                    : null;
        }

        boolean contains(String name) {
            return this.values.containsKey(name);
        }

        void checkDuplicate(final String name, int line, int column) {
            //XXX: recheck
            if (this.values.containsKey(name)) {
                throw new ParseException("Duplicate Variant declare: ".concat(name), line, column);
            }
        }

        void assignConst(final String name, final Object value, int line, int column) {
            checkDuplicate(name, line, column);
            if (this.constMap == null) {
                this.constMap = new HashMap<String, Object>();
            }
            this.values.put(name, -1);
            this.constMap.put(name, value);
        }

        Integer assignVar(final String name, int line, int column) {
            checkDuplicate(name, line, column);
            //XXX: recheck
            int index = VariantManager.this.varCount++;
            this.values.put(name, index);
            return index;
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
