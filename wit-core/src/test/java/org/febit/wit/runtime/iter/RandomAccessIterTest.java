package org.febit.wit.runtime.iter;

import org.junit.jupiter.api.Test;

import java.util.List;

class RandomAccessIterTest {

    @Test
    void list() {
        IterAsserts.empty(RandomAccessIter.of(List.of()));
        IterAsserts.abc(RandomAccessIter.of(List.of("a", "b", "c")));
    }

    @Test
    void stringArray() {
        IterAsserts.empty(RandomAccessIter.of(new String[]{}));
        IterAsserts.abc(RandomAccessIter.of(new String[]{"a", "b", "c"}));
    }

    @Test
    void objectArray() {
        IterAsserts.empty(RandomAccessIter.of(new Object[]{}));
        IterAsserts.abc(RandomAccessIter.of(new Object[]{"a", "b", "c"}));
    }

    @Test
    void chars() {
        IterAsserts.empty(RandomAccessIter.of(""));
        IterAsserts.empty(RandomAccessIter.ofArray("".toCharArray()));
        IterAsserts.abcChars(RandomAccessIter.of("abc"));
        IterAsserts.abcChars(RandomAccessIter.ofArray("abc".toCharArray()));
    }

}
