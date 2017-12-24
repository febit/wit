// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.core.ast.statements;

import java.util.List;
import org.febit.wit.InternalContext;
import org.febit.wit.core.LoopInfo;
import org.febit.wit.core.ast.Loopable;
import org.febit.wit.core.ast.Statement;
import org.febit.wit.util.StatementUtil;

/**
 *
 * @author zqq90
 */
public final class Break extends Statement implements Loopable {

    private final int label;

    public Break(int label, int line, int column) {
        super(line, column);
        this.label = label;
    }

    @Override
    public Object execute(final InternalContext context) {
        context.breakLoop(label);
        return null;
    }

    @Override
    public List<LoopInfo> collectPossibleLoops() {
        return StatementUtil.asList(new LoopInfo(LoopInfo.BREAK, label, line, column));
    }
}
