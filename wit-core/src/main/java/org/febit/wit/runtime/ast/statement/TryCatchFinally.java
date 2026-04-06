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
package org.febit.wit.runtime.ast.statement;

import org.febit.wit.runtime.FlowControl;
import org.febit.wit.runtime.FlowControls;
import org.febit.wit.runtime.RuntimeContext;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.ast.Statement;
import org.febit.wit.runtime.ast.WithFlowControl;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

public record TryCatchFinally(
        int exceptionVarSlot,
        Statement body,
        Statement catchBody,
        @Nullable Statement finallyBody,
        Position position
) implements Statement, WithFlowControl {

    @Override
    @Nullable
    public Object execute(RuntimeContext context) {
        try {
            body.execute(context);
        } catch (Exception e) {
            context.variables().set(exceptionVarSlot, e);
            catchBody.execute(context);
        } finally {
            if (finallyBody != null) {
                finallyBody.execute(context);
            }
        }
        return null;
    }

    @Override
    public void bubbleFlowControls(Consumer<FlowControl> collector) {
        FlowControls.bubble(collector, body, catchBody, finallyBody);
    }
}
