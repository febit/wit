// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.parser;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.ast.FlowControl;
import org.febit.wit.runtime.ast.FlowControls;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.ast.Statement;
import org.febit.wit.runtime.ast.expr.FunctionDeclarer;
import org.febit.wit.runtime.ast.statement.StatementBatch;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.febit.wit.util.Defaults.nvl;

@Accessors(fluent = true, chain = true)
public abstract class BaseForInBuilder {

    protected final VarLayout varLayout;
    protected final Position position;

    private boolean frameShifted = false;

    @Getter
    @Setter
    private int label;

    @Nullable
    protected List<Statement> body;

    @Getter
    private int frame;

    @Setter
    @Nullable
    protected Statement elseBody;

    @Setter
    @Nullable
    protected Expression collection;

    @Setter
    @Nullable
    protected FunctionDeclarer filter;

    protected int iterIndex;

    protected BaseForInBuilder(VarLayout varLayout, Position position) {
        this.position = position;
        this.varLayout = varLayout;
    }

    public abstract Statement build();

    public BaseForInBuilder shiftFrame() {
        if (frameShifted) {
            throw new IllegalStateException("frame already shifted");
        }
        this.frameShifted = true;
        varLayout.shiftFrame();
        iterIndex = varLayout.assignVar("for.iter", position);
        return this;
    }

    public BaseForInBuilder body(@Nullable List<Statement> list) {
        if (!frameShifted) {
            throw new IllegalStateException("frame not shifted");
        }
        this.frame = varLayout.unshiftFrame();
        this.body = nvl(list, List::of);
        return this;
    }

    protected BodiesInspect inspectBodies() {
        var bodyCtrl = new ArrayList<FlowControl>();
        var batches = Ast.batch(body, bodyCtrl::add);

        var bubbled = bodyCtrl.stream()
                .filter(FlowControls.loopBubbleFilter(label))
                .collect(Collectors.toCollection(ArrayList::new));

        if (elseBody != null) {
            FlowControls.collect(bubbled::add, elseBody);
        }
        return new BodiesInspect(!bodyCtrl.isEmpty(), batches, List.copyOf(bubbled));
    }

    protected record BodiesInspect(
            boolean isBodyHasFlowControls,
            List<StatementBatch> bodyBatches,
            List<FlowControl> bubbledFlowControls
    ) {
    }

}
