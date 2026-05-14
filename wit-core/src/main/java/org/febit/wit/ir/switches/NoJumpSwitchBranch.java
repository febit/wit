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
package org.febit.wit.ir.switches;

import org.febit.wit.ir.Position;
import org.febit.wit.ir.Statement;
import org.febit.wit.runtime.RuntimeContext;
import org.febit.wit.runtime.Undefined;

public record NoJumpSwitchBranch(
        Statement body
) implements SwitchBranch {

    @Override
    public Object execute(RuntimeContext context) {
        body.execute(context);
        return Undefined.UNDEFINED;
    }

    @Override
    public Position position() {
        return body.position();
    }
}
