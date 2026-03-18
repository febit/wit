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
package org.febit.wit.runtime.iter;

import java.util.function.BiPredicate;

public class FilteredKeyIter<I extends KeyIter>
        extends FilteredIter<I>
        implements KeyIter {

    protected FilteredKeyIter(I iter, BiPredicate<I, Object> filter) {
        super(iter, filter);
    }

    @Override
    public Object value() {
        return iter.value();
    }
}
