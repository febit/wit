// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.core;

/**
 *
 * @author zqq90
 */
public class LoopInfo {

    public static final int BREAK = 1;
    public static final int CONTINUE = 2;
    public static final int RETURN = 3;

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

    public boolean matchLabel(int label) {
        return this.label == 0 || this.label == label;
    }

    @Override
    public String toString() {
        int myType = this.type;
        return (myType == BREAK ? "break"
                : myType == CONTINUE ? "continue"
                        : myType == RETURN ? "return"
                                : "null") + "{" + "label=" + label + ", line=" + line + ", column=" + column + '}';
    }
}
