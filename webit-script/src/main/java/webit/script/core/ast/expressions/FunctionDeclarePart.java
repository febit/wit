// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.expressions;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import webit.script.core.VariantIndexer;
import webit.script.core.VariantManager;
import webit.script.core.ast.Position;
import webit.script.core.ast.Statement;
import webit.script.core.ast.StatementList;
import webit.script.core.ast.loop.LoopInfo;
import webit.script.exceptions.ParseException;
import webit.script.util.StatementUtil;
import webit.script.util.StringUtil;

/**
 *
 * @author Zqq
 */
public final class FunctionDeclarePart extends Position {

    private int argsCount = 0;
    private final VariantManager varmgr;

    public FunctionDeclarePart(VariantManager varmgr, int line, int column) {
        super(line, column);
        this.varmgr = varmgr;
        varmgr.push();
        varmgr.pushVarWall();
        if (varmgr.assignVariant("arguments", line, column) != 0) {
            throw new ParseException("assignVariant failed!");
        }
    }

    public FunctionDeclarePart appendArg(String name, int line, int column) {
        if (varmgr.assignVariant(name, line, column) != (++ this.argsCount)) {
            throw new ParseException("assignVariant failed!");
        }
        return this;
    }

    public FunctionDeclare pop(VariantManager varmgr, StatementList list) {
        Statement[] statements = list.toInvertArray();

        int[] overflowUpstairs = varmgr.popVarWall();
        Map<String, Integer> varIndexer = varmgr.pop();

        boolean hasReturnLoops = false;
        List<LoopInfo> loopInfos = StatementUtil.collectPossibleLoopsInfo(statements);

        if (loopInfos != null) {

            for (Iterator<LoopInfo> it = loopInfos.iterator(); it.hasNext();) {
                LoopInfo loopInfo = it.next();
                if (loopInfo.type == LoopInfo.RETURN) {
                    hasReturnLoops = true;
                    it.remove();
                }
            }
            if (loopInfos.size() > 0) {
                throw new ParseException("Loops overflow in function body: ".concat(StringUtil.join(loopInfos, ",")));
            }
        }

        return new FunctionDeclare(argsCount,
                overflowUpstairs != null && overflowUpstairs.length != 0 ? overflowUpstairs : null,
                VariantIndexer.getVariantIndexer(varIndexer),
                statements,
                hasReturnLoops,
                line, column);
    }
}
