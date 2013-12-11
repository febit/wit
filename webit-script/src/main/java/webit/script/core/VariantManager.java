// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;
import webit.script.Engine;
import webit.script.exceptions.ParseException;
import webit.script.global.GlobalManager;
import webit.script.util.StringUtil;
import webit.script.util.collection.ArrayStack;
import webit.script.util.collection.Stack;

/**
 *
 * @author Zqq
 */
public class VariantManager {

    private Map<String, Integer>[] elements;
    private int currentElementIndex;
    private final Stack<VarWall> varWallStack;

    private final GlobalManager globalManager;

    @SuppressWarnings("unchecked")
    public VariantManager(Engine engine) {
        this.varWallStack = new ArrayStack<VarWall>();
        this.globalManager = engine.getGlobalManager();

        this.elements = new Map[10];
        Map<String, Integer> root = elements[0] = createNewMap(); //current
        String[] vars;
        if ((vars = engine.getVars()) != null) {
            String var;
            for (int i = 0, len = vars.length; i < len; i++) {
                var = vars[i];
                if (!root.containsKey(var)) {
                    root.put(var, root.size());
                }//ignore duplicate
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
        Map<String, Integer>[] _elements;
        if ((i = ++currentElementIndex) >= (_elements = elements).length) {
            System.arraycopy(_elements, 0,
                    _elements = elements = new Map[i << 1], 0, i);
        }
        _elements[i] = createNewMap();
    }

    protected final Map<String, Integer> createNewMap() {
        return new HashMap<String, Integer>();
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
            Map<String, Integer> element = elements[index];
            elements[index] = null;
            return element;
        } else {
            throw new ParseException("Variant stack overflow");
        }
    }

    public VarAddress assignVariantAddress(String name, int line, int column) {
        return VarAddress.context(0, assignVariant(name, line, column), currentElementIndex == 0);
    }

    public int assignVariant(String name, int line, int column) {
        Map<String, Integer> current;
        if (!(current = elements[currentElementIndex]).containsKey(name)) {
            int address;
            current.put(name, (address = current.size()));
            return address;
        } else {
            throw new ParseException("Duplicate Variant declare: ".concat(name), line, column);
        }
    }

    public VarAddress assignVariantAtRoot(final String name, int line, int column) {
        //int topindex = varWallStack.peek().value;
        final Map<String, Integer> top;
        if ((top = elements[0]).containsKey(name)) {
            throw new ParseException("Duplicate Variant declare: ".concat(name), line, column);
        }
        checkVarWall(0);
        final int address;
        top.put(name, address = top.size());
        return VarAddress.context(currentElementIndex, address, true);
    }

    public VarAddress locateAtUpstair(String name, int upstair, int line, int column) {
        if (upstair <= currentElementIndex) {
            final int i = currentElementIndex - upstair;
            Integer index;
            if ((index = elements[i].get(name)) != null) {
                checkVarWall(i);
                return VarAddress.context(currentElementIndex - i, index, i == 0);
            }
            throw new ParseException("Can't locate variant: ".concat(name), line, column);
        } else {
            throw new ParseException(StringUtil.concat("Stack overflow when locate variant in given upstair: ", name, "-", Integer.toString(upstair)), line, column);
        }
    }

    public VarAddress locate(String name, int fromUpstair, boolean force, int line, int column) {
        for (int i = currentElementIndex - fromUpstair; i >= 0; --i) {
            Integer index;
            if ((index = elements[i].get(name)) != null) {
                checkVarWall(i);
                return VarAddress.context(currentElementIndex - i, index, i == 0);
            }
        }

        int index;
        if ((index = this.globalManager.getVariantIndex(name)) >= 0) {
            return VarAddress.global(index);
        } else if (this.globalManager.hasConst(name)) {
            return VarAddress.constValue(this.globalManager.getConst(name));
        }

        if (force) {
            throw new ParseException("Can't locate variant: ".concat(name), line, column);
        } else {
            return assignVariantAtRoot(name, line, column);
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

        public final static int CONTEXT = 0;
        public final static int ROOT = 1;
        public final static int GLOBAL = 2;
        public final static int CONST = 3;

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
