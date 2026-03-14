package org.febit.wit.runtime.iter;

import lombok.experimental.UtilityClass;
import org.febit.wit.exception.ScriptEvaluateException;
import org.febit.wit.runtime.ALU;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.WitFunction;
import org.febit.wit.runtime.ast.Statement;
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
            return RandomAccessIter.of(list);
        }
        var clazz = o1.getClass();
        if (clazz.isArray()) {
            if (o1 instanceof Object[] arr) {
                return RandomAccessIter.of(arr);
            }
            return RandomAccessIter.ofArray(o1);
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
            return RandomAccessIter.of(cs);
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

    public static Iter ofFiltered(InternalContext context, Iter iter, WitFunction function) {
        return new FilteredIter<>(iter, (i, pending) -> ALU.isTruly(
                function.apply(context, new @Nullable Object[]{pending})
        ));
    }

    public static KeyIter ofFiltered(InternalContext context, KeyIter iter, WitFunction function) {
        return new FilteredKeyIter<>(iter, (i, pending) -> ALU.isTruly(
                function.apply(context, new @Nullable Object[]{pending, i.value()})
        ));
    }
}
