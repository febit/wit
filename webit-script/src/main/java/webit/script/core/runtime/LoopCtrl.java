// Copyright (c) 2013, Webit Team. All Rights Reserved.

package webit.script.core.runtime;

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
        goon = false;
        _label = label;
        loopValue = null;
        loopType = LoopType.BREAK;
        this.statment = statment;
    }

    public void continueLoop(String label, Statment statment) {
        goon = false;
        _label = label;
        loopValue = null;
        loopType = LoopType.CONTINUE;
        this.statment = statment;
    }

    public void returnLoop(Object value, Statment statment) {
        goon = false;
        _label = null;
        loopValue = value;
        loopType = LoopType.RETURN;
        this.statment = statment;
    }

    public void reset() {
        loopValue = null;
        _label = null;
        loopType = LoopType.NULL;
        goon = true;
        statment = null;
    }

    public boolean goon() {
        return goon;
    }

    public LoopType getLoopType() {
        return loopType;
    }

    public Object getLoopValue() {
        return loopValue;
    }

    public Statment getStatment() {
        return statment;
    }
}
