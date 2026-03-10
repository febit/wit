// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.parser;

import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.ast.Statement;
import org.febit.wit.runtime.ast.StatementUtils;
import org.febit.wit.runtime.ast.statement.ForMap;
import org.febit.wit.runtime.ast.statement.ForMapNonFlow;

import java.util.Objects;

public class ForMapBuilder extends BaseForInBuilder {

    private final String keyVarName;
    private final String valueVarName;

    private int keyIndex;
    private int valueIndex;

    public ForMapBuilder(
            String keyVarName,
            String valueVarName,
            VarLayout varLayout,
            Position position
    ) {
        super(varLayout, position);
        this.keyVarName = keyVarName;
        this.valueVarName = valueVarName;
    }

    @Override
    public final ForMapBuilder shiftFrame() {
        super.shiftFrame();
        this.keyIndex = varLayout.assignVar(keyVarName, position);
        this.valueIndex = varLayout.assignVar(valueVarName, position);
        return this;
    }

    @Override
    public Statement build() {
        Objects.requireNonNull(body);
        Objects.requireNonNull(collection);

        var collection = StatementUtils.optimize(this.collection);

        var bodiesInspect = inspectBodies();
        var bodyBatches = bodiesInspect.bodyBatches();
        if (!bodiesInspect.isBodyHasFlowControls()) {
            if (bodyBatches.size() != 1) {
                throw new IllegalStateException("unexpected body batches size: " + bodyBatches.size());
            }
            return new ForMapNonFlow(frame(), collection, filter,
                    iterIndex, keyIndex, valueIndex, bodyBatches.get(0), elseBody, position);
        }

        return new ForMap(label(), frame(), collection, filter,
                iterIndex, keyIndex, valueIndex, bodyBatches,
                elseBody, bodiesInspect.bubbledFlowControls(),
                position);
    }
}
