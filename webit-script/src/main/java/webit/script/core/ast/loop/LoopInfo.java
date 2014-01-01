// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core.ast.loop;

/**
 *
 * @author Zqq
 */
public class LoopInfo {

    public final static int NO_LOOP = 0;
    public final static int BREAK = 1;
    public final static int CONTINUE = 2;
    public final static int RETURN = 3;
    //
    public final static int NO_LABEL = 0;
    //
    private final static String[] LOOP_NAMES = new String[]{"null", "break", "continue", "return"};
    //
    public final int type;
    public final int label;
    public final int line;
    public final int column;

    public LoopInfo(int type, int label, int line, int column) {
        this.type = type;
        this.label = label;
        this.line = line;
        this.column = column;
    }

    public boolean matchLabel(int aLabel) {
        return label == 0 || label == aLabel;
    }

    @Override
    public String toString() {
        return LOOP_NAMES[type] + "{" + "label=" + label + ", line=" + line + ", column=" + column + '}';
    }

    public static String getLoopName(int loop) {
        return LOOP_NAMES[loop];
    }
}
