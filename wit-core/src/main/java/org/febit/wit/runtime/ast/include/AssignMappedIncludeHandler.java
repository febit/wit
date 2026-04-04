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
package org.febit.wit.runtime.ast.include;

import org.febit.wit.Context;
import org.febit.wit.runtime.RuntimeContext;
import org.febit.wit.runtime.ast.Assignable;

import java.util.List;

public record AssignMappedIncludeHandler(
        List<Entry> entries
) implements IncludeHandler {

    public AssignMappedIncludeHandler {
        entries = List.copyOf(entries);
    }

    public record Entry(
            String variable,
            Assignable target
    ) {
    }

    @Override
    public void process(RuntimeContext parent, Context included) {
        var source = included.variables();
        for (var entry : entries) {
            var value = source.get(entry.variable);
            entry.target.assign(parent, value);
        }
    }
}
