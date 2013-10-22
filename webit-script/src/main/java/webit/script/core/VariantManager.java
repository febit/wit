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
    private Stack<VarWall> varWallStack = new ArrayStack<VarWall>();

    public final void pushVarWall() {
        varWallStack.push(new VarWall(currentElementIndex));
    }

    public int[] popVarWall() {
        return varWallStack.pop().getOverflowUpstairs();
    }

    @SuppressWarnings("unchecked")
    public VariantManager() {

        elements = new Map[10];
        elements[0] = createNewMap(); //current
        elements[1] = createNewMap(); //next

        currentElementIndex = 0;
        pushVarWall();
    }

    @SuppressWarnings("unchecked")
    private void ensureCapacity(int mincap) {
        if (mincap > elements.length) {
            int newcap = ((elements.length * 3) >> 1) + 1;
            Object[] olddata = elements;
            elements = new Map[newcap < mincap ? mincap : newcap];
            System.arraycopy(olddata, 0, elements, 0, currentElementIndex + 1);
        }
    }

    public void push() {

        ensureCapacity(currentElementIndex + 3);

        ++currentElementIndex; // next => current
        elements[currentElementIndex + 1] = createNewMap(); // next
    }

    protected Map<String, Integer> next() {
        return elements[currentElementIndex + 1];
    }

    protected Map<String, Integer> current() {
        return elements[currentElementIndex];
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
            for (Iterator<Integer> it = overflowUpstairSet.iterator(); it.hasNext();) {
                overflowUpstairs[i++] = it.next();
            }
            return overflowUpstairs;
        }
    }

    public Map<String, Integer> pop() {
        int index;
        if ((index = currentElementIndex) >= 0) {
            Map<String, Integer>[] _elements;
            Map<String, Integer> element = (_elements = elements)[index]; //the one need pop 
            _elements[index + 1] = null; // clear old next
            _elements[index] = createNewMap(); // this is 'next' now
            currentElementIndex = index - 1; //back point
            return element;
        } else {
            throw new ParseException("Variant stack overflow");
        }
    }

    public int assignVariant(String name, int line, int column) {
        Map<String, Integer> current;
        if (!(current = current()).containsKey(name)) {
            int address;
            current.put(name, (address = current.size()));
            return address;
        } else {
            throw new ParseException("Duplicate Variant declare: ".concat(name), line, column);
        }
    }

    public VarAddress assignVariantAtTopWall(String name) {
        int topindex = varWallStack.peek().value;
        Map<String, Integer> top = elements[topindex];
        if (top.containsKey(name)) {
            throw new ParseException("Duplicate Variant declare: ".concat(name));
        }
        int address = top.size();
        top.put(name, address);
        return new VarAddress(currentElementIndex - topindex, address);
    }

    public int assignVariantForNextBlock(String name) {
        Map<String, Integer> next = next();
        if (next.containsKey(name)) {
            throw new ParseException("Duplicate Variant declare: ".concat(name));
        }
        int address = next.size();
        next.put(name, address);
        return address;
    }

    public int locateAtUpstair(String name, int upstair, boolean force, int line, int column) {
        if (upstair <= currentElementIndex) {
            int i = currentElementIndex - upstair;
            Map<String, Integer> map = elements[i];
            Integer index = map.get(name);
            if (index != null) {
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
            Integer index = elements[i].get(name);
            if (index != null) {
                checkVarWall(i);
                return new VarAddress(currentElementIndex - i, index);
            }
        }
        if (force) {
            throw new ParseException("Can't locate variant: ".concat(name), line, column);
        } else {
            return assignVariantAtTopWall(name);
        }
    }

    public VarAddress locate(String name, int line, int column) {
        return locate(name, false, line, column);
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
