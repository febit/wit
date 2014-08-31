// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core.ast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import webit.script.core.VariantManager;
import webit.script.core.ast.loop.LoopInfo;
import webit.script.core.ast.statements.Block;
import webit.script.core.ast.statements.BlockNoLoops;
import webit.script.core.ast.statements.IBlock;
import webit.script.core.ast.statements.StatementGroup;
import webit.script.exceptions.ParseException;
import webit.script.util.ArrayUtil;
import webit.script.util.StatementUtil;
import webit.script.util.StringUtil;

/**
 *
 * @author Zqq
 */
public final class StatementList {

    private final ArrayList<Statement> statementList;

    public StatementList() {
        this.statementList = new ArrayList<Statement>();
    }

    public StatementList add(Statement statement) {
        if (statement instanceof StatementGroup) {
            statementList.addAll(Arrays.asList(((StatementGroup) statement).getList()));
        } else if ((statement = StatementUtil.optimize(statement)) != null) {
            statementList.add(statement);
        }
        return this;
    }

    public Statement[] toArray() {
        return this.statementList.toArray(new Statement[statementList.size()]);
    }

    public Statement[] toInvertArray() {
        final Statement[] statements = toArray();
        ArrayUtil.invert(statements);
        return statements;
    }

    public Statement popStatementGroup(int line, int column) {
        return new StatementGroup(toArray(), line, column);
    }

    public TemplateAST popTemplateAST(VariantManager varmgr) {
        Statement[] statements = this.toInvertArray();
        List<LoopInfo> loopInfos;
        if ((loopInfos = StatementUtil.collectPossibleLoopsInfo(statements)) == null) {
            return new TemplateAST(varmgr.getIndexers(), statements, varmgr.getVarCount());
        } else {
            throw new ParseException("loop overflow: ".concat(StringUtil.join(loopInfos, ',')));
        }
    }

    public IBlock popIBlock(int varIndexer, int line, int column) {
        Statement[] statements;

        List<LoopInfo> loopInfoList = StatementUtil.collectPossibleLoopsInfo(
                statements = this.toInvertArray());

        return loopInfoList != null
                ? new Block(varIndexer, statements, loopInfoList.toArray(new LoopInfo[loopInfoList.size()]), line, column)
                : new BlockNoLoops(varIndexer, statements, line, column);
    }

}
