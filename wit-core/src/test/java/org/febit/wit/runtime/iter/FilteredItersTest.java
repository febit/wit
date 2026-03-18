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

import org.febit.lang.util.Lists;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.util.Args;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.mockito.Mockito.mock;

class FilteredItersTest {

    private final InternalContext context = mock(InternalContext.class);

    @Test
    void empty() {
        IterAsserts.empty(Iters.ofFiltered(context,
                Iters.empty(),
                (context, args) -> true)
        );

        IterAsserts.empty(Iters.ofFiltered(context,
                RandomAccessIter.of("abc"),
                (context, args) -> false)
        );
    }

    private static boolean isAbc(InternalContext context, Object[] args) {
        var str = (String) Args.at(args, 0);
        if (str == null) {
            return false;
        }
        return switch (str) {
            case "a", "b", "c" -> true;
            default -> false;
        };
    }

    @Test
    void abc() {
        IterAsserts.abc(Iters.ofFiltered(context,
                RandomAccessIter.of(List.of("a", "b", "c")),
                (context, args) -> true)
        );
        IterAsserts.abc(Iters.ofFiltered(context,
                RandomAccessIter.of(Lists.collect(new String[]{
                        "0", "a", "1", "b", "c", null, "d", "e"
                })),
                FilteredItersTest::isAbc
        ));
    }

    @Test
    void keyIter() {
        var map = new TreeMap<>(Map.of(
                "a", 1,
                "b", 2,
                "c", 3,
                "d", 4,
                "e", 5
        ));
        IterAsserts.abc(Iters.ofFiltered(
                context,
                MapKeyIter.of(map),
                FilteredItersTest::isAbc
        ));

        IterAsserts.abc(Iters.ofFiltered(
                context,
                MapKeyIter.of(map),
                (context, args) -> {
                    var v = (Integer) Args.at(args, 1);
                    return v != null && v >= 1 && v <= 3;
                }
        ));
    }

}
