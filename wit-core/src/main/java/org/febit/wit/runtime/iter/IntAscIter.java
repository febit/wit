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

import java.util.NoSuchElementException;

/**
 * An Iterator of int from low to high, inclusive.
 * e.g. of(1, 3) will produce 1, 2, 3.
 * of(1, 1) will produce 1.
 * of(2, 1) will produce nothing.
 */
public class IntAscIter implements Iter {

    private final int from;
    private final int to;
    private int current;

    private IntAscIter(int from, int to) {
        this.from = from;
        this.to = to;
        current = from - 1;
    }

    public static Iter of(int from, int to) {
        return new IntAscIter(from, to);
    }

    @Override
    public boolean hasNext() {
        return current < to;
    }

    @Override
    public Integer next() {
        if (current >= to) {
            throw new NoSuchElementException("no more next");
        }
        return ++current;
    }

    @Override
    public int index() {
        return current - from;
    }
}
