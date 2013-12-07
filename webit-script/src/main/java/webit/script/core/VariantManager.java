// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;
import webit.script.exceptions.ParseException;
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

    @SuppressWarnings("unchecked")
    public VariantManager(String[] vars) {
        this.varWallStack = new ArrayStack<VarWall>();

        elements = new Map[10];
        Map<String, Integer> root = elements[0] = createNewMap(); //current
        if (vars != null) {
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

    public VarAddress assignVariantAtRoot(final String name) {
        //int topindex = varWallStack.peek().value;
        final Map<String, Integer> top;
        if ((top = elements[0]).containsKey(name)) {
            throw new ParseException("Duplicate Variant declare: ".concat(name));
        }
        checkVarWall(0);
        final int address;
        top.put(name, address = top.size());
        return new VarAddress(currentElementIndex, address);
    }

    public int locateAtUpstair(String name, int upstair, boolean force, int line, int column) {
        if (upstair <= currentElementIndex) {
            final int i = currentElementIndex - upstair;
            Integer index;
            if ((index = elements[i].get(name)) != null) {
                checkVarWall(i);
                return index;
            }
            if (force) {
                throw new ParseException("Can't locate variant: ".concat(name), line, column);
            } else {
                return -1;
            }
        } else {
            throw new ParseException(StringUtil.concat("Stack overflow when locate variant in given upstair: ", name, "-", Integer.toString(upstair)), line, column);
        }
    }

    public VarAddress locate(String name, boolean force, int line, int column) {
        for (int i = currentElementIndex; i >= 0; --i) {
            Integer index;
            if ((index = elements[i].get(name)) != null) {
                checkVarWall(i);
                return new VarAddress(currentElementIndex - i, index);
            }
        }
        if (force) {
            throw new ParseException("Can't locate variant: ".concat(name), line, column);
        } else {
            return assignVariantAtRoot(name);
        }
    }

    public VarAddress locate(String name, int line, int column) {
        return locate(name, false, line, column);
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

        public final int upstairs;
        public final int index;

        public VarAddress(int upstairs, int index) {
            this.upstairs = upstairs;
            this.index = index;
        }
    }
}
