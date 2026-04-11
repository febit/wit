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
package org.febit.wit.ir.include;

import lombok.experimental.UtilityClass;
import org.febit.wit.Context;
import org.febit.wit.runtime.RuntimeContext;

@UtilityClass
public class IncludeHandlers {

    public static void noop(RuntimeContext parent, Context included) {
        // do nothing
    }

    public static void importAll(RuntimeContext parent, Context included) {
        var target = parent.variables();
        included.variables().forEach(target::set);
    }
}
