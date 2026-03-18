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
package org.febit.wit.runtime.ast.extra;

import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.ast.AssignableExpression;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.ast.Position;
import org.jspecify.annotations.Nullable;

public final class Import extends AbstractInclude {

    private final String[] exportVars;
    private final AssignableExpression @Nullable [] targets;
    private final boolean exportAll;

    public Import(
            Expression pathExpr,
            @Nullable Expression paramsExpr,
            String @Nullable [] exportVars,
            AssignableExpression @Nullable [] targets,
            String refer,
            Position position
    ) {
        super(pathExpr, paramsExpr, refer, position);
        if (exportVars == null || exportVars.length == 0) {
            this.exportVars = new String[0];
            this.targets = null;
            this.exportAll = true;
        } else {
            this.exportVars = exportVars;
            this.targets = targets;
            this.exportAll = false;
        }
    }

    @Override
    @Nullable
    @SuppressWarnings("UnnecessaryLocalVariable")
    public Object execute(InternalContext context) {
        var results = mergeScript(context, true);
        if (exportAll) {
            results.forEach(context.variables()::set);
            return null;
        }
        if (this.targets != null) {
            var names = this.exportVars;
            var len = names.length;
            var assignables = this.targets;
            for (int i = 0; i < len; i++) {
                assignables[i].assign(context, results.get(names[i]));
            }
        }
        return null;
    }
}
