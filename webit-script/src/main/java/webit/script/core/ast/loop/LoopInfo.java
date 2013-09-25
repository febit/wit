// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.loop;

/**
 *
 * @author Zqq
 */
public class LoopInfo {

    public final LoopType type;
    public final String label;
    public final int line;
    public final int column;

    public LoopInfo(LoopType type, String label, int line, int column) {
        this.type = type;
        this.label = label;
        this.line = line;
        this.column = column;
    }

    public boolean matchLabel(String aLabel) {
        return label == null ? true : label.equals(aLabel);
    }

    @Override
    public String toString() {
        return type.value + "{" + "label=" + label + ", line=" + line + ", column=" + column + '}';
    }
}
