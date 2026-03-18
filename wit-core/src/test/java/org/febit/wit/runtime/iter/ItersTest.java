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

import org.febit.wit.exception.ScriptEvaluateException;
import org.febit.wit.runtime.ast.Statement;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class ItersTest {

    private final Statement statement = mock(Statement.class);

    @Test
    void keyIter() {
        assertInstanceOf(EmptyIter.class, Iters.ofKeyIter(null, statement));
        assertInstanceOf(MapKeyIter.class, Iters.ofKeyIter(Map.of("a", 1), statement));
        assertThrows(ScriptEvaluateException.class, () -> Iters.ofKeyIter("abc", statement));
    }

    @Test
    void iter() {
        assertInstanceOf(EmptyIter.class, Iters.ofIter(null, statement));
        assertInstanceOf(RandomAccessIter.class, Iters.ofIter(new String[]{"a", "b"}, statement));
        assertInstanceOf(RandomAccessIter.class, Iters.ofIter(new int[]{1, 2}, statement));
        assertInstanceOf(RandomAccessIter.class, Iters.ofIter("abc", statement));

        assertInstanceOf(RandomAccessIter.class, Iters.ofIter(List.of("a", "b"), statement));
        assertInstanceOf(RandomAccessIter.class, Iters.ofIter(new ArrayList<>(List.of("a", "b")), statement));

        assertInstanceOf(IteratorIter.class, Iters.ofIter(new LinkedList<>(List.of("a", "b")), statement));
        assertInstanceOf(IteratorIter.class, Iters.ofIter(Set.of("a", "b"), statement));
        assertInstanceOf(IteratorIter.class, Iters.ofIter(List.of("a", "b").iterator(), statement));

        assertInstanceOf(EnumerationIter.class, Iters.ofIter(new Vector<>(List.of("a", "b")).elements(), statement));

        var desc = IntDescIter.of(1, 2);
        assertSame(desc, Iters.ofIter(desc, statement));

        assertThrows(ScriptEvaluateException.class, () -> Iters.ofIter(123, statement));
    }

}
