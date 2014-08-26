// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core.ast.loop;

import webit.script.Context;

/**
 *
 * @author Zqq
 */
public final class LoopCtrl {

    private Object value = null;
    private int label = LoopInfo.NO_LABEL;
    private int type = LoopInfo.NO_LOOP;

    public boolean matchLabel(int label) {
        return this.label == LoopInfo.NO_LABEL || this.label == label;
    }

    public void breakLoop(int label) {
        this.label = label;
        this.type = LoopInfo.BREAK;
    }

    public void continueLoop(int label) {
        this.label = label;
        this.type = LoopInfo.CONTINUE;
    }

    public void returnLoop(Object value) {
        this.value = value;
        this.label = LoopInfo.NO_LABEL;
        this.type = LoopInfo.RETURN;
    }

    public void reset() {
        this.value = null;
        this.label = LoopInfo.NO_LABEL;
        this.type = LoopInfo.NO_LOOP;
    }

    public void resetBreakLoopIfMatch(int label) {
        //TODO: rethink
        if (this.type == LoopInfo.BREAK && (this.label == LoopInfo.NO_LABEL || this.label == label)) {
            this.reset();
        }
    }

    public Object resetReturnLoop() {
        Object result = this.type == LoopInfo.RETURN ? this.value : Context.VOID;
        this.value = null;
        this.label = LoopInfo.NO_LABEL;
        this.type = LoopInfo.NO_LOOP;
        return result;
    }

    public int getLoopType() {
        return this.type;
    }
}
