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

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.List;
import java.util.NoSuchElementException;

@RequiredArgsConstructor(staticName = "of")
public class RandomAccessIter implements Iter {

    private final int max;
    private final Getter getter;

    private int index = -1;

    public static Iter of(CharSequence seq) {
        if (seq.isEmpty()) {
            return Iters.empty();
        }
        return of(seq.length() - 1, seq::charAt);
    }

    public static Iter of(List<?> list) {
        if (list.isEmpty()) {
            return Iters.empty();
        }
        return of(list.size() - 1, list::get);
    }

    public static Iter of(Object[] array) {
        if (array.length == 0) {
            return Iters.empty();
        }
        return of(array.length - 1, i -> array[i]);
    }

    public static Iter ofArray(Object array) {
        var len = Array.getLength(array);
        if (len == 0) {
            return Iters.empty();
        }
        return of(len - 1, i -> Array.get(array, i));
    }

    @Override
    public int index() {
        return index;
    }

    @Override
    public boolean hasNext() {
        return index < max;
    }

    @Override
    public @Nullable Object next() {
        if (!hasNext()) {
            throw new NoSuchElementException("no more next");
        }
        return getter.get(++index);
    }

    @FunctionalInterface
    public interface Getter {
        @Nullable
        Object get(int value);
    }

}
