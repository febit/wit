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

import org.febit.wit.ir.Located;
import org.febit.wit.ir.Statement;
import org.febit.wit.ir.loop.ForMap;
import org.febit.wit.ir.support.StatementUtils;
import org.febit.wit.parser.IR;

import java.util.Objects;

public class ForMapBuilder extends BaseForInBuilder {

    private final String keyVarName;
    private final String valueVarName;

    private int keySlot;
    private int valueSlot;

    public ForMapBuilder(
            String keyVarName,
            String valueVarName,
            VarLayout varLayout,
            Located located
    ) {
        super(varLayout, located);
        this.keyVarName = keyVarName;
        this.valueVarName = valueVarName;
    }

    @Override
    public final ForMapBuilder shiftScope() {
        super.shiftScope();
        this.keySlot = varLayout.assignVar(keyVarName, located);
        this.valueSlot = varLayout.assignVar(valueVarName, located);
        return this;
    }

    @Override
    public Statement build() {
        Objects.requireNonNull(body);
        Objects.requireNonNull(collection);

        var collection = StatementUtils.optimize(this.collection);
        var loopBody = IR.loopBodyFromStatements(this.body, label());
        return new ForMap(
                scope(), collection, filter,
                iterSlot, keySlot, valueSlot,
                loopBody, elseBody,
                located.position()
        );
    }
}
