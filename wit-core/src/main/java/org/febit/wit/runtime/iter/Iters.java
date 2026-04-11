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

import lombok.experimental.UtilityClass;
import org.febit.wit.exception.ScriptEvaluateException;
import org.febit.wit.ir.Statement;
import org.febit.wit.ir.support.ALU;
import org.febit.wit.runtime.RuntimeContext;
import org.febit.wit.runtime.WitFunction;
import org.jspecify.annotations.Nullable;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.RandomAccess;

@UtilityClass
public class Iters {

    private static final KeyIter EMPTY = new EmptyIter();

    public static KeyIter empty() {
        return EMPTY;
    }

    @SuppressWarnings({
            "squid:S3776" // Cognitive Complexity of methods should not be too high
    })
    public static Iter ofIter(@Nullable final Object o1, Statement refer) {
        if (o1 == null) {
            return empty();
        }
        if (o1 instanceof Iter iter) {
            return iter;
        }
        if (o1 instanceof List<?> list && list instanceof RandomAccess) {
            return SequenceIter.of(list);
        }
        var clazz = o1.getClass();
        if (clazz.isArray()) {
            if (o1 instanceof Object[] arr) {
                return SequenceIter.of(arr);
            }
            return SequenceIter.ofArray(o1);
        }
        if (o1 instanceof Iterable) {
            return IteratorIter.of(((Iterable<?>) o1).iterator());
        }
        if (o1 instanceof Iterator) {
            return IteratorIter.of((Iterator<?>) o1);
        }
        if (o1 instanceof Enumeration) {
            return EnumerationIter.of((Enumeration<?>) o1);
        }
        if (o1 instanceof CharSequence cs) {
            return SequenceIter.of(cs);
        }
        throw new ScriptEvaluateException("Unsupported type to Iter: " + o1.getClass(), refer);
    }

    public static KeyIter ofKeyIter(@Nullable final Object o1, Statement refer) {
        if (o1 == null) {
            return empty();
        }
        if (o1 instanceof Map<?, ?> map) {
            if (map.isEmpty()) {
                return empty();
            }
            return MapKeyIter.of(map);
        }
        throw new ScriptEvaluateException("Unsupported type to KeyIter: " + o1.getClass(), refer);
    }

    public static Iter ofFiltered(RuntimeContext context, Iter iter, WitFunction function) {
        return new FilteredIter<>(iter, (i, pending) -> ALU.isTruly(
                function.apply(context, new @Nullable Object[]{pending})
        ));
    }

    public static KeyIter ofFiltered(RuntimeContext context, KeyIter iter, WitFunction function) {
        return new FilteredKeyIter<>(iter, (i, pending) -> ALU.isTruly(
                function.apply(context, new @Nullable Object[]{pending, i.value()})
        ));
    }
}
