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
package org.febit.wit.extern.servlet.accessor;

import org.febit.wit.extern.servlet.facade.HttpServletRequestHeader;
import org.febit.wit.runtime.accessor.Getter;
import org.jspecify.annotations.Nullable;

public class HttpServletRequestHeaderAccessor implements Getter<HttpServletRequestHeader> {
    @Nullable
    @Override
    public Object get(HttpServletRequestHeader bean, @Nullable Object property) {
        if (property == null) {
            return null;
        }
        return bean.get(property.toString());
    }

}
