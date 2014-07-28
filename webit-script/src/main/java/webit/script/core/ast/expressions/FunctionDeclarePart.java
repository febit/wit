// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core.ast.expressions;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import webit.script.core.VariantIndexer;
import webit.script.core.VariantManager;
import webit.script.core.ast.Expression;
import webit.script.core.ast.Position;
import webit.script.core.ast.Statement;
import webit.script.core.ast.StatementList;
import webit.script.core.ast.loop.LoopInfo;
import webit.script.core.ast.operators.Assign;
import webit.script.exceptions.ParseException;
import webit.script.util.StatementUtil;
import webit.script.util.StringUtil;

/**
 *
 * @author Zqq
 */
public class FunctionDeclarePart extends Position {

    private int argsCount = 0;
    private final int assignToIndex;
    private final VariantManager varmgr;

    public FunctionDeclarePart(String assignTo, VariantManager varmgr, int line, int column) {
        this(varmgr.assignVariant(assignTo, line, column), varmgr, line, column);
    }

    public FunctionDeclarePart(VariantManager varmgr, int line, int column) {
        this(-1, varmgr, line, column);
    }

    public FunctionDeclarePart(int assignToIndex, VariantManager varmgr, int line, int column) {
        super(line, column);
        this.varmgr = varmgr;
        this.assignToIndex = assignToIndex;
        varmgr.push();
        varmgr.pushVarWall();
        if (varmgr.assignVariant("arguments", line, column) != 0) {
            throw new ParseException("assignVariant failed!");
        }
    }

    public FunctionDeclarePart appendArg(String name, int line, int column) {
        if (varmgr.assignVariant(name, line, column) != (++this.argsCount)) {
            throw new ParseException("assignVariant failed!");
        }
        return this;
    }
    
    public Expression pop(StatementList list) {
        final Expression expr = popFunctionDeclare(list);
        if (this.assignToIndex >= 0) {
            return new Assign(new CurrentContextValue(this.assignToIndex, line, column), expr, line, column);
        } else {
            return expr;
        }
    }

    public FunctionDeclare popFunctionDeclare(StatementList list) {
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
