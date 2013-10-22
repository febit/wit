// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import webit.script.core.VariantManager;
import webit.script.core.ast.expressions.FunctionDeclareExpression;
import webit.script.core.ast.loop.LoopInfo;
import webit.script.core.ast.method.FunctionPart;
import webit.script.core.ast.statments.BlockStatment;
import webit.script.core.ast.statments.BlockStatmentNoLoops;
import webit.script.core.ast.statments.IBlockStatment;
import webit.script.exceptions.ParseException;
import webit.script.util.ArrayUtil;
import webit.script.util.StatmentUtil;
import webit.script.util.StringUtil;
import webit.script.util.VariantUtil;

/**
 *
 * @author Zqq
 */
public final class StatmentList {

    private final List<Statment> statmentList;

    public StatmentList() {
        this.statmentList = new LinkedList<Statment>();
    }

    public StatmentList add(Statment stat) {
        if ((stat = StatmentUtil.optimize(stat)) != null) {
            statmentList.add(stat);
        }
        return this;
    }

    public Statment[] toInvertArray() {
        Statment[] statments = this.statmentList.toArray(new Statment[statmentList.size()]);
        ArrayUtil.invert(statments);
        return statments;
    }

    public TemplateAST popTemplateAST(Map<String, Integer> varMap) {

        Statment[] statments = this.toInvertArray();

        List<LoopInfo> loopInfos;
        if ((loopInfos = StatmentUtil.collectPossibleLoopsInfo(statments)) == null) {
            return new TemplateAST(VariantUtil.toVariantMap(varMap), statments);
        } else {
            throw new ParseException("loop overflow: ".concat(StringUtil.join(loopInfos, ",")));
        }
    }

    public IBlockStatment popIBlockStatment(Map<String, Integer> varMap, int line, int column) {
        Statment[] statments;

        List<LoopInfo> loopInfoList = StatmentUtil.collectPossibleLoopsInfo(
                statments = this.toInvertArray());

        return loopInfoList != null
                ? new BlockStatment(VariantUtil.toVariantMap(varMap), statments, loopInfoList.toArray(new LoopInfo[loopInfoList.size()]), line, column)
                : new BlockStatmentNoLoops(VariantUtil.toVariantMap(varMap), statments, line, column);
    }

}
