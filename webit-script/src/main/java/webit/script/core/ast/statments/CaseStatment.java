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

    public CaseStatment(BlockStatment body, CaseStatment next, int line, int column) {
        super(line, column);
        this.body = body;
        this.next = next;
    }

    public void execute(final Context context) {
        if (body != null) {
            StatmentUtil.execute(body, context);
        }
        if (context.loopCtrl.goon() && next != null) {
            StatmentUtil.execute(next, context);
        }
    }

    public boolean isBodyEmpty() {
        return (body == null) && (next == null);
    }
}
