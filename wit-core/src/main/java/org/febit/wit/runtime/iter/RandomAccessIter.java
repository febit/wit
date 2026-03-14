// Copyright (c) 2013-present, febit.org. All Rights Reserved.
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
