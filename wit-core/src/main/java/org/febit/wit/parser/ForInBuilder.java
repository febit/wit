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

import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.ast.Statement;
import org.febit.wit.runtime.ast.StatementUtils;
import org.febit.wit.runtime.ast.loop.ForIn;

import java.util.Objects;

public class ForInBuilder extends BaseForInBuilder {

    private final String itemVarName;
    private int itemSlot;

    public ForInBuilder(String itemVarName, VarLayout varLayout, Position position) {
        super(varLayout, position);
        this.itemVarName = itemVarName;
    }

    @Override
    public final ForInBuilder shiftScope() {
        super.shiftScope();
        itemSlot = varLayout.assignVar(itemVarName, position);
        return this;
    }

    @Override
    public Statement build() {
        Objects.requireNonNull(this.body);
        Objects.requireNonNull(this.collection);

        var collection = StatementUtils.optimize(this.collection);
        var loopBody = Ast.loopBodyFromStatements(this.body, label());
        return new ForIn(
                scope(), collection, filter,
                iterSlot, itemSlot,
                loopBody, elseBody,
                position
        );
    }
}
