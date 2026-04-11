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
package org.febit.wit.parser.support;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.febit.wit.ir.Expression;
import org.febit.wit.ir.Position;
import org.febit.wit.ir.Statement;
import org.febit.wit.ir.expr.FunctionLiteral;
import org.jspecify.annotations.Nullable;

import java.util.List;

import static org.febit.wit.parser.ReservedNames.FOR_ITER;
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
    protected FunctionLiteral filter;

    protected int iterSlot;

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
        iterSlot = varLayout.assignVar(FOR_ITER, position);
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
}
