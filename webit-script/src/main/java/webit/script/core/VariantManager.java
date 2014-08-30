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

    private int indexerCount;
    private int varCount;
    private final Stack<IndexerRaw> rawStack;
    private final List<IndexerRaw> raws;
    private final IndexerRaw root;
    private final GlobalManager globalManager;

    @SuppressWarnings("unchecked")
    public VariantManager(Engine engine) {
        this.indexerCount = 0;
        this.globalManager = engine.getGlobalManager();
        this.raws = new ArrayList<IndexerRaw>();
        this.rawStack = new Stack<IndexerRaw>();
        final IndexerRaw root = this.root = new IndexerRaw(-1);
        this.rawStack.push(root);
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

    @SuppressWarnings("unchecked")
    public void push() {
        IndexerRaw parent = rawStack.peek();
        this.rawStack.push(new IndexerRaw(parent.id));
    }

    public int getIndexerCount() {
        return indexerCount;
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
            final Map<String, Object> constMap = raw.constMap;
            if (constMap != null) {
                for (Map.Entry<String, Object> entry : constMap.entrySet()) {
                    indexerMap.remove(entry.getKey());
                }
            }
            VariantIndexer parent = raw.parentId >= 0 ? result[raw.parentId] : null;
            result[i] = getVariantIndexer(i, parent, indexerMap);
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
        return VarAddress.context(assignVariant(name, line, column));
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
        return VarAddress.context(address);
    }

    public VarAddress locateAtUpstair(String name, int upstair, int line, int column) {
        try {
            IndexerRaw raw = rawStack.peek(upstair);
            Integer index;
            if ((index = raw.getIndex(name)) != null) {
                if (index >= 0) {
                    return VarAddress.context(index);
                } else {
                    return VarAddress.constValue(raw.getConstValue(name));
                }
            }
            throw new ParseException("Can't locate vars: ".concat(name), line, column);
        } catch (IndexOutOfBoundsException e) {
            throw new ParseException(StringUtil.format("Stack overflow when locate vars '{}' at upstair {}", name, upstair), line, column);
        }
    }

    public VarAddress locate(String name, int fromUpstair, boolean force, int line, int column) {
        for (; fromUpstair < rawStack.size(); fromUpstair++) {
            IndexerRaw raw = rawStack.peek(fromUpstair);
            Integer index;
            if ((index = raw.getIndex(name)) != null) {
                if (index >= 0) {
                    return VarAddress.context(index);
                } else {
                    return VarAddress.constValue(raw.getConstValue(name));
                }
            }
        }

        int index;
        if ((index = this.globalManager.getGlobalIndex(name)) >= 0) {
            return VarAddress.global(index);
        } else if (this.globalManager.hasConst(name)) {
            return VarAddress.constValue(this.globalManager.getConst(name));
        }

        if (force) {
            throw new ParseException("Can't locate vars: ".concat(name), line, column);
        } else {
            return assignVariantAtRoot(name, line, column);
        }
    }

    private class IndexerRaw {

        final int id;
        final int parentId;
        final Map<String, Integer> values;
        Map<String, Object> constMap;

        IndexerRaw(int parentId) {
            this.parentId = parentId;
            this.values = new HashMap<String, Integer>();
            this.id = VariantManager.this.indexerCount++;
            VariantManager.this.raws.add(this);
        }

        Integer getIndex(String name) {
            return this.values.get(name);
        }

        Object getConstValue(String name) {
            return this.constMap != null
                    ? this.constMap.get(name)
                    : null;
        }

        private void prepareConstMap() {
            if (this.constMap == null) {
                this.constMap = new HashMap<String, Object>();
            }
        }

        boolean contains(String name) {
            return this.values.containsKey(name);
        }

        void checkDuplicate(final String name, int line, int column) {
            if (this.values.containsKey(name)) {
                throw new ParseException("Duplicate Variant declare: ".concat(name), line, column);
            }
        }

        void assignConst(final String name, final Object value, int line, int column) {
            checkDuplicate(name, line, column);
            prepareConstMap();
            this.values.put(name, -1);
            this.constMap.put(name, value);
        }

        Integer assignVar(final String name, int line, int column) {
            checkDuplicate(name, line, column);
            int index = VariantManager.this.varCount++;
            this.values.put(name, index);
            return index;
        }
    }

    public static class VarAddress {

        public static final int CONTEXT = 0;
        public static final int GLOBAL = 2;
        public static final int CONST = 3;

        public final int index;
        public final int type;
        public final Object constValue;

        public VarAddress(int index, int type, Object constValue) {
            this.index = index;
            this.type = type;
            this.constValue = constValue;
        }

        public static VarAddress context(int index) {
            return new VarAddress(index, CONTEXT, null);
        }

        public static VarAddress global(int index) {
            return new VarAddress(index, GLOBAL, null);
        }

        public static VarAddress constValue(Object value) {
            return new VarAddress(-1, CONST, value);
        }
    }
}
