package webit.script.core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;
import webit.script.exceptions.ParserException;
import webit.script.util.ArrayStack;
import webit.script.util.Stack;

/**
 *
 * @author Zqq
 */
public class VariantManager {

    private Map<String, Integer>[] elements;
    private int currentElementIndex;
    //private Stack<TreeSet<Integer>> overflowUpstairSetStack = new ArrayStack<TreeSet<Integer>>();
    //private TreeSet<Integer> overflowUpstairSet = new TreeSet<Integer>();
    //private int currentVarWall = 0;
    //private Stack<Integer> varWallStack = new ArrayStack<Integer>();
    private Stack<VarWall> varWallStack = new ArrayStack<VarWall>();

    public int pushVarWall() {
        varWallStack.push(new VarWall(currentElementIndex));
        return currentElementIndex;
    }

    public int[] popVarWall() {
        VarWall varWall = varWallStack.pop();
        return varWall.getOverflowUpstairs();
    }

    public VariantManager() {

        elements = new Map[10];
        elements[0] = createNewMap(); //current
        elements[1] = createNewMap(); //next

        currentElementIndex = 0;
    }

    public VariantManager(Map<String, Integer> root) {

        elements = new Map[10];
        elements[0] = root;
        elements[1] = createNewMap(); //current
        elements[2] = createNewMap(); //next

        currentElementIndex = 1;
    }

    public void ensureCapacity(int mincap) {
        if (mincap > elements.length) {
            int newcap = ((elements.length * 3) >> 1) + 1;
            Object[] olddata = elements;
            elements = new HashMap[newcap < mincap ? mincap : newcap];
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
        for (int i = 0; i < varWallStack.size(); i++) {
            if (varWallStack.peek(i).checkOverflow(index) == false) {
                break;
            }
        }
    }

    private static class VarWall {

        int value;
        TreeSet<Integer> overflowUpstairSet;

        public VarWall(int value) {
            this.value = value;
            overflowUpstairSet = new TreeSet<Integer>();
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
        if (currentElementIndex < 0) {
            throw new ParserException("Variant stack overflow");
        }

        Map<String, Integer> element = elements[currentElementIndex]; //the one need pop 
        elements[currentElementIndex + 1] = null; // clear old next
        elements[currentElementIndex] = createNewMap(); // this is 'next' now
        --currentElementIndex; //back point

        return element;
    }

    public int assignVariant(String name) {
        Map<String, Integer> current = current();
        if (current.containsKey(name)) {
            throw new ParserException("Duplicate Variant declare: " + name);
        }
        int address = current.size();
        current.put(name, address);
        return address;
    }

    public int assignVariantForNextBlock(String name) {
        Map<String, Integer> next = next();
        if (next.containsKey(name)) {
            throw new ParserException("Duplicate Variant declare: " + name);
        }
        int address = next.size();
        next.put(name, address);
        return address;
    }

    public int locateAtUpstair(String name, int upstair, boolean force) {
        if (upstair <= currentElementIndex) {
            int i = currentElementIndex - upstair;
            Map<String, Integer> map = elements[i];
            Integer index = map.get(name);
            if (index != null) {
                checkVarWall(i);
                return index;
            }
            if (force) {
                throw new ParserException("Can't locate variant: " + name);
            } else {
                return -1;
            }
        } else {
            throw new ParserException("Stack overflow when locate variant in given upstair: " + name + '-' + upstair);
        }
    }

    public VarAddress locate(String name, boolean force) {
        for (int i = currentElementIndex; i >= 0; --i) {
            Map<String, Integer> map = elements[i];
            Integer index = map.get(name);
            if (index != null) {
                checkVarWall(i);
                return new VarAddress(currentElementIndex - i, index);
            }
        }
        if (force) {
            throw new ParserException("Can't locate variant: " + name);
        } else {
            return null;
        }
    }

    public VarAddress locate(String name) {
        return locate(name, false);
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
