// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.parser;

import org.febit.wit.runtime.ast.AstUtils;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.ast.Statement;
import org.febit.wit.runtime.ast.statement.ForMap;
import org.febit.wit.runtime.ast.statement.ForMapNonFlow;

import java.util.Arrays;
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
    public Statement build(int label) {
        Objects.requireNonNull(body);
        Objects.requireNonNull(collection);

        var collection = AstUtils.optimize(this.collection);

        var hasFlowControl = AstUtils.hasFlowControls(body);
        if (!hasFlowControl) {
            return new ForMapNonFlow(filter, collection, frame(),
                    iterIndex, keyIndex, valueIndex, body, elseBody, position);
        }

        var controls = AstUtils.flowControlsOverLoop(label, Arrays.asList(body), elseBody);
        return new ForMap(filter, collection, frame(),
                iterIndex, keyIndex, valueIndex, body,
                controls, elseBody, label, position);
    }
}
