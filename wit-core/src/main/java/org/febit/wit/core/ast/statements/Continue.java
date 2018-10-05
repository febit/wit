// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.core.ast.statements;

import org.febit.wit.InternalContext;
import org.febit.wit.core.LoopInfo;
import org.febit.wit.core.ast.Loopable;
import org.febit.wit.core.ast.Statement;

import java.util.Collections;
import java.util.List;

/**
 * @author zqq90
 */
public final class Continue extends Statement implements Loopable {

    private final int label;

    public Continue(int label, int line, int column) {
        super(line, column);
        this.label = label;
    }

    @Override
    public Object execute(final InternalContext context) {
        context.continueLoop(label);
        return null;
    }

    @Override
    public List<LoopInfo> collectPossibleLoops() {
        return Collections.singletonList(new LoopInfo(LoopInfo.CONTINUE, label, line, column));
    }
}
