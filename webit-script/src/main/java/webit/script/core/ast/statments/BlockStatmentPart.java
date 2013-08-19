// Copyright (c) 2013, Webit Team. All Rights Reserved.

package webit.script.core.ast.statments;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import webit.script.core.ast.Statment;
import webit.script.core.ast.StatmentPart;
import webit.script.core.runtime.variant.VariantUtil;

/**
 *
 * @author Zqq
 */
public final class BlockStatmentPart extends StatmentPart {

    protected final List<Statment> statmentList;
    private Map<String, Integer> varMap;

    public BlockStatmentPart(int line, int column) {
        super(line, column);
        this.statmentList = new ArrayList();
    }

    public BlockStatmentPart setVarMap(Map<String, Integer> varMap) {
        this.varMap = varMap;
        return this;
    }

    public BlockStatmentPart append(Statment stat) {
        if (stat != null) {
            statmentList.add(stat);
        }
        return this;
    }

    public BlockStatment pop() {
        if (statmentList.isEmpty()) {
            return new BlockStatment(null, null, line, column);
        }
        Statment[] statments = new Statment[statmentList.size()];
        statmentList.toArray(statments);
        return new BlockStatment(VariantUtil.toVariantMap(varMap), statments, line, column).optimize();
    }
}
