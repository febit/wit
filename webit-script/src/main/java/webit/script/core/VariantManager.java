// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;
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

    private VarIndexer[] elements;
    private int currentElementIndex;
    private final Stack<VarWall> varWallStack;

    private final GlobalManager globalManager;

    @SuppressWarnings("unchecked")
    public VariantManager(Engine engine) {
        this.varWallStack = new Stack<VarWall>();
        this.globalManager = engine.getGlobalManager();

        this.elements = new VarIndexer[10];
        final VarIndexer root = elements[0] = new VarIndexer(); //current
        final String[] vars = engine.getVars();
        if (vars != null) {
            for (String var : vars) {
                if (!root.contains(var)) {
                    root.assignVar(var, -1, -1);
                }
                //ignore duplicate
            }
        }

        currentElementIndex = 0;
        pushVarWall();
    }

    public final void pushVarWall() {
        varWallStack.push(new VarWall(currentElementIndex));
    }

    public int[] popVarWall() {
        return varWallStack.pop().getOverflowUpstairs();
    }

    @SuppressWarnings("unchecked")
    public void push() {
        final int i;
        VarIndexer[] _elements;
        if ((i = ++currentElementIndex) >= (_elements = elements).length) {
            System.arraycopy(_elements, 0,
                    _elements = elements = new VarIndexer[i << 1], 0, i);
        }
        _elements[i] = new VarIndexer();
    }

    public void checkVarWall(int index) {
        for (int i = 0, len = varWallStack.size(); i < len; i++) {
            if (varWallStack.peek(i).checkOverflow(index) == false) {
                break;
            }
        }
    }

    public Map<String, Integer> pop() {
        int index;
        if ((index = currentElementIndex--) >= 0) {
            final VarIndexer element = elements[index];
            elements[index] = null;
            //remove consts
            final Map<String, Integer> indexerMap = element.values;
            final Map<String, Object> constMap = element.constMap;
            if (constMap != null) {
                for (Map.Entry<String, Object> entry : constMap.entrySet()) {
                    indexerMap.remove(entry.getKey());
                }
            }
            return indexerMap;
        } else {
            throw new ParseException("Variant stack overflow");
        }
    }

    public VarAddress assignVariantAddress(String name, int line, int column) {
        return VarAddress.context(0, assignVariant(name, line, column), currentElementIndex == 0);
    }

    public int assignVariant(String name, int line, int column) {
        return elements[currentElementIndex].assignVar(name, line, column);
    }

    public void assignConst(String name, Object value, int line, int column) {
        elements[currentElementIndex].assignConst(name, value, line, column);
    }

    public VarAddress assignVariantAtRoot(final String name, int line, int column) {
        final VarIndexer top;
        (top = elements[0]).checkDuplicate(name, line, column); //XXX： remove？
        checkVarWall(0);
        final int address = top.assignVar(name, line, column);
        return VarAddress.context(currentElementIndex, address, true);
    }

    public VarAddress locateAtUpstair(String name, int upstair, int line, int column) {
        if (upstair <= currentElementIndex) {
            final int i = currentElementIndex - upstair;
            Integer index;
            if ((index = elements[i].getIndex(name)) != null) {
                if (index >= 0) {
                    checkVarWall(i);
                    return VarAddress.context(currentElementIndex - i, index, i == 0);
                } else {
                    return VarAddress.constValue(elements[i].getConstValue(name));
                }
            }
            throw new ParseException("Can't locate vars: ".concat(name), line, column);
        } else {
            throw new ParseException(StringUtil.format("Stack overflow when locate vars '{}' at upstair {}", name, upstair), line, column);
        }
    }

    public VarAddress locate(String name, int fromUpstair, boolean force, int line, int column) {
        for (int i = currentElementIndex - fromUpstair; i >= 0; --i) {
            Integer index;
            if ((index = elements[i].getIndex(name)) != null) {
                if (index >= 0) {
                    checkVarWall(i);
                    return VarAddress.context(currentElementIndex - i, index, i == 0);
                } else {
                    return VarAddress.constValue(elements[i].getConstValue(name));
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

    private static class VarIndexer {

        final Map<String, Integer> values;
        Map<String, Object> constMap;
        private int varIndex;

        VarIndexer() {
            this.values = new HashMap<String, Integer>();
            this.varIndex = 0;
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
            int index = this.varIndex++;
            this.values.put(name, index);
            return index;
        }
    }

    private static class VarWall {

        int value;
        TreeSet<Integer> overflowUpstairSet;

        VarWall(int value) {
            this.value = value;
            this.overflowUpstairSet = new TreeSet<Integer>();
        }

        public boolean checkOverflow(int index) {
            if (index < value) {
                overflowUpstairSet.add(value - index - 1);
                return true;
            } else {
                return false;
            }
        }

        public int[] getOverflowUpstairs() {
            int[] overflowUpstairs = new int[overflowUpstairSet.size()];
            int i = 0;
            Iterator<Integer> it = overflowUpstairSet.iterator();
            while (it.hasNext()) {
                overflowUpstairs[i++] = it.next();
            }
            return overflowUpstairs;
        }
    }

    public static class VarAddress {

        public static final int CONTEXT = 0;
        public static final int ROOT = 1;
        public static final int GLOBAL = 2;
        public static final int CONST = 3;

        public final int upstairs;
        public final int index;
        public final int type;
        public final Object constValue;

        public VarAddress(int upstairs, int index, int type, Object constValue) {
            this.upstairs = upstairs;
            this.index = index;
            this.type = type;
            this.constValue = constValue;
        }

        public static VarAddress context(int upstairs, int index, boolean isRoot) {
            return new VarAddress(upstairs, index, isRoot ? ROOT : CONTEXT, null);
        }

        public static VarAddress global(int index) {
            return new VarAddress(-1, index, GLOBAL, null);
        }

        public static VarAddress constValue(Object value) {
            return new VarAddress(-1, -1, CONST, value);
        }
    }
}
