// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.statments;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import webit.script.Context;
import webit.script.core.VariantIndexer;
import webit.script.core.ast.AbstractStatment;
import webit.script.core.ast.Statment;
import webit.script.core.ast.loop.LoopInfo;
import webit.script.core.ast.loop.Loopable;
import webit.script.core.runtime.VariantStack;
import webit.script.util.StatmentUtil;

/**
 *
 * @author Zqq
 */
public final class BlockStatment extends AbstractStatment implements Loopable, IBlockStatment {

    public final VariantIndexer varIndexer;
    public final Statment[] statments;
    public final LoopInfo[] possibleLoopsInfo;

    public BlockStatment(VariantIndexer varIndexer, Statment[] statments, LoopInfo[] possibleLoopsInfo, int line, int column) {
        super(line, column);
        this.varIndexer = varIndexer;
        this.statments = statments;
        this.possibleLoopsInfo = possibleLoopsInfo;
    }

    public Object execute(final Context context) {
        final VariantStack vars;
        (vars = context.vars).push(varIndexer);
        StatmentUtil.executeInvertedAndCheckLoops(statments, context);
        vars.pop();
        return null;
    }

    public List<LoopInfo> collectPossibleLoopsInfo() {
        return new LinkedList<LoopInfo>(Arrays.asList(possibleLoopsInfo));
    }

    public boolean hasLoops() {
        return true;
    }

    public VariantIndexer getVarMap() {
        return varIndexer;
    }

    public Statment[] getStatments() {
        return statments;
    }
}
