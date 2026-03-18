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
package org.febit.wit.util;

import java.util.ArrayList;

public class Stack<T> extends ArrayList<T> {

    public Stack() {
        super();
    }

    public Stack(int initialCapacity) {
        super(initialCapacity);
    }

    public void pops(int count) {
        final int size = size();
        removeRange(size - count, size);
    }

    public void push(final T element) {
        add(element);
    }

    public T pop() {
        return remove(size() - 1);
    }

    public T peek(int offset) {
        return get(size() - offset - 1);
    }

    public T peek() {
        return peek(0);
    }
}
