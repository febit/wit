// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.loop;

import webit.script.core.ast.Statment;

/**
 *
 * @author Zqq
 */
public final class LoopCtrl {

    private LoopType loopType = LoopType.NULL;
    private boolean goon = true;
    private Object loopValue = null;
    private String _label = null;
    private Statment statment;

    public boolean mathBreakLoop(String label) {
        if (!goon && loopType == LoopType.BREAK) {
            return _label == null ? true : _label.equals(label);
        }
        return false;
    }
    
    public boolean matchLabel(String label){
        return !goon && (_label == null ? true : _label.equals(label));
    }

    public boolean mathContinueLoop(String label) {
        if (!goon && loopType == LoopType.CONTINUE) {
            return loopValue == null ? true : loopValue.equals(label);
        }
        return false;
    }

    public void breakLoop(String label, Statment statment) {
        this.goon = false;
        this._label = label;
        this.loopValue = null;
        this.loopType = LoopType.BREAK;
        this.statment = statment;
    }

    public void continueLoop(String label, Statment statment) {
        this.goon = false;
        this._label = label;
        this.loopValue = null;
        this.loopType = LoopType.CONTINUE;
        this.statment = statment;
    }

    public void returnLoop(Object value, Statment statment) {
        this.goon = false;
        this._label = null;
        this.loopValue = value;
        this.loopType = LoopType.RETURN;
        this.statment = statment;
    }

    public void reset() {
        this.loopValue = null;
        this._label = null;
        this.loopType = LoopType.NULL;
        this.goon = true;
        this.statment = null;
    }

    public boolean goon() {
        return this.goon;
    }

    public LoopType getLoopType() {
        return this.loopType;
    }

    public Object getLoopValue() {
        return this.loopValue;
    }

    public Statment getStatment() {
        return this.statment;
    }
}
