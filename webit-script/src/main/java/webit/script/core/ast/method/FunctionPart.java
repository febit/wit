// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.method;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import webit.script.core.VariantManager;
import webit.script.core.ast.Position;
import webit.script.core.ast.Statment;
import webit.script.core.ast.StatmentList;
import webit.script.core.ast.expressions.FunctionDeclareExpression;
import webit.script.core.ast.loop.LoopInfo;
import webit.script.exceptions.ParseException;
import webit.script.util.StatmentUtil;
import webit.script.util.StringUtil;
import webit.script.util.VariantUtil;
import webit.script.util.collection.IntArrayList;

/**
 *
 * @author Zqq
 */
public final class FunctionPart extends Position {

    private final IntArrayList argIndexList; //with arguments at first
    private int argsIndex;

    public FunctionPart setArgsIndex(int argsIndex) {
        this.argsIndex = argsIndex;
        return this;
    }

    public FunctionPart(int line, int column) {
        super(line, column);
        this.argIndexList = new IntArrayList(6);
    }

    public FunctionPart appendArgIndexs(int index) {
        this.argIndexList.add(index);
        return this;
    }

    public FunctionDeclareExpression pop(VariantManager varmgr, StatmentList list) {
        Statment[] statments = list.toInvertArray();

        int[] overflowUpstairs = varmgr.popVarWall();
        Map<String, Integer> varMap = varmgr.pop();

        boolean hasReturnLoops = false;
        List<LoopInfo> loopInfos = StatmentUtil.collectPossibleLoopsInfo(statments);

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

        return new FunctionDeclareExpression(new Function(argsIndex,
                argIndexList.isEmpty() ? null : argIndexList.toArray(),
                overflowUpstairs != null && overflowUpstairs.length != 0 ? overflowUpstairs : null,
                VariantUtil.toVariantMap(varMap),
                statments,
                hasReturnLoops,
                line, column));
    }
}
