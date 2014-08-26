// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core.ast.loop;

/**
 *
 * @author Zqq
 */
public class LoopInfo {

    public static final int NO_LOOP = 0;
    public static final int BREAK = 1;
    public static final int CONTINUE = 2;
    public static final int RETURN = 3;

    public static final int NO_LABEL = 0;

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
        return getLoopName(type) + "{" + "label=" + label + ", line=" + line + ", column=" + column + '}';
    }

    public static String getLoopName(int loop) {
        switch (loop) {
            case 1:
                return "break";
            case 2:
                return "continue";
            case 3:
                return "return";
            default:
                return "null";
        }
    }
}
