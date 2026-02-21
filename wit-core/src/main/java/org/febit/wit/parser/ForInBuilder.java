// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.parser;

import org.febit.wit.runtime.ast.AstUtils;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.ast.Statement;
import org.febit.wit.runtime.ast.stat.ForIn;
import org.febit.wit.runtime.ast.stat.ForInNoLoops;

import java.util.Arrays;
import java.util.Objects;

public class ForInBuilder extends BaseForInBuilder {

    private final String itemVarName;
    private int itemIndex;

    public ForInBuilder(String itemVarName, VarLayout varLayout, Position position) {
        super(varLayout, position);
        this.itemVarName = itemVarName;
    }

    @Override
    public final ForInBuilder shiftFrame() {
        super.shiftFrame();
        itemIndex = varLayout.assignVar(itemVarName, position);
        return this;
    }

    @Override
    public Statement build(int label) {
        Objects.requireNonNull(this.body);
        Objects.requireNonNull(this.collection);

        var collection = AstUtils.optimize(this.collection);

        var loops = AstUtils.collectLoopFlagsForWhile(Arrays.asList(body), elseBody, label);
        if (loops.length == 0) {
            return new ForInNoLoops(filter, collection, frame(),
                    iterIndex, itemIndex, body, elseBody, position);
        }

        return new ForIn(filter, collection, frame(),
                iterIndex, itemIndex, body,
                loops, elseBody, label, position
        );
    }
}
