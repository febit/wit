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
import webit.script.util.collection.IntArrayList;

/**
 *
 * @author Zqq
 */
public final class FunctionPart extends Position {

    private final IntArrayList argIndexList; //with arguments at first
    private final int argsIndex;

    public FunctionPart(int argsIndex, int line, int column) {
        super(line, column);
        this.argsIndex = argsIndex;
        this.argIndexList = new IntArrayList(6);
    }

    public FunctionPart appendArgIndexs(int index) {
        this.argIndexList.add(index);
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

        return new FunctionDeclare(argsIndex,
                argIndexList.isEmpty() ? null : argIndexList.toArray(),
                overflowUpstairs != null && overflowUpstairs.length != 0 ? overflowUpstairs : null,
                VariantIndexer.getVariantIndexer(varIndexer),
                statements,
                hasReturnLoops,
                line, column);
    }
}
