// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.parser;

import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.ast.Statement;
import org.febit.wit.runtime.ast.StatementUtils;
import org.febit.wit.runtime.ast.statement.ForIn;
import org.febit.wit.runtime.ast.statement.ForInNonFlow;

import java.util.Objects;

public class ForInBuilder extends BaseForInBuilder {

    private final String itemVarName;
    private int itemIndex;

    public ForInBuilder(String itemVarName, VarLayout varLayout, Position position) {
        super(varLayout, position);
        this.itemVarName = itemVarName;
    }

    @Override
    public final ForInBuilder shiftScope() {
        super.shiftScope();
        itemIndex = varLayout.assignVar(itemVarName, position);
        return this;
    }

    @Override
    public Statement build() {
        Objects.requireNonNull(this.body);
        Objects.requireNonNull(this.collection);

        var collection = StatementUtils.optimize(this.collection);

        var bodiesInspect = inspectBodies();
        var bodyBatches = bodiesInspect.bodyBatches();
        if (!bodiesInspect.isBodyHasFlowControls()) {
            if (bodyBatches.size() != 1) {
                throw new IllegalStateException("unexpected body batches size: " + bodyBatches.size());
            }
            return new ForInNonFlow(scope(), collection, filter,
                    iterIndex, itemIndex, bodyBatches.get(0), elseBody, position);
        }

        return new ForIn(label(), scope(), collection, filter,
                iterIndex, itemIndex, bodyBatches,
                elseBody, bodiesInspect.bubbledFlowControls(),
                position
        );
    }
}
