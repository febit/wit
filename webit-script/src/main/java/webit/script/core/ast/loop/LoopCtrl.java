// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core.ast.loop;

import webit.script.Context;

/**
 *
 * @author Zqq
 */
public final class LoopCtrl {

    private Object _loopValue = null;
    private int _label = LoopInfo.NO_LABEL;
    private int _loopType = LoopInfo.NO_LOOP;

    public boolean matchLabel(int label) {
        return this._label == LoopInfo.NO_LABEL || this._label == label;
    }

    public void breakLoop(int label) {
        //this.loopValue = null;
        this._label = label;
        this._loopType = LoopInfo.BREAK;
    }

    public void continueLoop(int label) {
        //this.loopValue = null;
        this._label = label;
        this._loopType = LoopInfo.CONTINUE;
    }

    public void returnLoop(Object value) {
        this._loopValue = value;
        this._label = LoopInfo.NO_LABEL;
        this._loopType = LoopInfo.RETURN;
    }

    public void reset() {
        this._loopValue = null;
        this._label = LoopInfo.NO_LABEL;
        this._loopType = LoopInfo.NO_LOOP;
    }

    public void resetBreakLoopIfMatch(int label) {
        if (this._loopType == LoopInfo.BREAK && (this._label == LoopInfo.NO_LABEL || this._label == label)) {
            this.reset();
        }
    }

    public Object resetReturnLoop() {
        Object result = this._loopType == LoopInfo.RETURN ? this._loopValue : Context.VOID;
        this._loopValue = null;
        this._label = LoopInfo.NO_LABEL;
        this._loopType = LoopInfo.NO_LOOP;
        return result;
    }

    public int getLoopType() {
        return this._loopType;
    }
}
