// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.statments;

import webit.script.Context;
import webit.script.core.ast.AbstractStatment;
import webit.script.util.StatmentUtil;

/**
 *
 * @author Zqq
 */
public final class CaseStatment extends AbstractStatment {

    private final BlockStatment body;
    private final CaseStatment next;
    private final boolean hasNext;

    public CaseStatment(BlockStatment body, CaseStatment next, int line, int column) {
        super(line, column);
        this.body = body;
        this.next = next;
        this.hasNext = next != null;
    }

    @Override
    public void execute(Context context) {
        if (body != null) {
            StatmentUtil.execute(body, context);
        }
        if (context.loopCtrl.goon() && hasNext) {
            StatmentUtil.execute(next, context);
        }
    }

    public boolean isBodyEmpty() {
        return (body == null) && (hasNext == false);
    }
}
