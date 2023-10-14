// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang;

import lombok.RequiredArgsConstructor;

/**
 * @author zqq90
 */
@RequiredArgsConstructor
public class LoopMeta {

    public static final int BREAK = 1;
    public static final int CONTINUE = 2;
    public static final int RETURN = 3;

    public final int type;
    public final int label;
    public final Position position;

    public boolean matchLabel(int label) {
        return this.label == 0 || this.label == label;
    }

    @Override
    public String toString() {
        int myType = this.type;
        return (myType == BREAK ? "break"
                : myType == CONTINUE ? "continue"
                : myType == RETURN ? "return"
                : "null") + "{" + "label=" + label + ", pos=" + position + '}';
    }
}
