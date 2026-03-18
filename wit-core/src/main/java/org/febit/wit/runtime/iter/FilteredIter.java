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

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;

import java.util.NoSuchElementException;
import java.util.function.BiPredicate;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class FilteredIter<I extends Iter> implements Iter {

    protected final I iter;
    private final BiPredicate<I, Object> filter;

    @Nullable
    private Object next;
    private boolean nextFlag;
    private int index = -1;

    @Override
    public int index() {
        return this.index;
    }

    @Nullable
    @Override
    public Object next() {
        if (!hasNext()) {
            throw new NoSuchElementException("no more next");
        }
        ++this.index;
        this.nextFlag = false;
        return this.next;
    }

    @Override
    public boolean hasNext() {
        if (this.nextFlag) {
            return true;
        }
        var it = this.iter;
        var fl = this.filter;
        while (it.hasNext()) {
            var pending = it.next();
            if (fl.test(it, pending)) {
                this.next = pending;
                this.nextFlag = true;
                return true;
            }
        }
        return false;
    }
}
