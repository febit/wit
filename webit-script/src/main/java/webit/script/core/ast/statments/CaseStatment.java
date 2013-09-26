// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.statments;

import java.util.List;
import webit.script.Context;
import webit.script.core.ast.loop.LoopInfo;
import webit.script.core.ast.loop.Loopable;
import webit.script.util.StatmentUtil;

/**
 *
 * @author Zqq
 */
public final class CaseStatment implements Loopable {

    private final BlockStatment body;
    private final CaseStatment next;

    public CaseStatment(BlockStatment body, CaseStatment next) {
        this.body = body;
        this.next = next;
    }

    public Object execute(final Context context) {
        if (body != null) {
            body.execute(context);
        }
        if (context.loopCtrl.goon() && next != null) {
            next.execute(context);
        }
        return null;
    }

    public boolean isBodyEmpty() {
        return (body == null) && (next == null);
    }

    public List<LoopInfo> collectPossibleLoopsInfo() {
        return StatmentUtil.collectPossibleLoopsInfo(body);
    }
}
