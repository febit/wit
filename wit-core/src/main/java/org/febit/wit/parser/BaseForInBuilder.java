/*
 * Copyright 2013-present febit.org (support@febit.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

import static org.febit.wit.Presets.FOR_ITER;
import static org.febit.wit.util.Defaults.nvl;

@Accessors(fluent = true, chain = true)
public abstract class BaseForInBuilder {

    protected final VarLayout varLayout;
    protected final Position position;

    private boolean scopeShifted = false;

    @Getter
    @Setter
    private int label;

    @Nullable
    protected List<Statement> body;

    @Getter
    private int scope;

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

    public BaseForInBuilder shiftScope() {
        if (scopeShifted) {
            throw new IllegalStateException("scope already shifted");
        }
        this.scopeShifted = true;
        varLayout.shiftScope();
        iterIndex = varLayout.assignVar(FOR_ITER, position);
        return this;
    }

    public BaseForInBuilder body(@Nullable List<Statement> list) {
        if (!scopeShifted) {
            throw new IllegalStateException("scope not shifted");
        }
        this.scope = varLayout.unshiftScope();
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
