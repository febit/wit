package org.febit.wit.runtime.iter;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

class IteratorIterTest {

    @Test
    void test() {
        IterAsserts.abc(IteratorIter.of(
                List.of("a", "b", "c").iterator()
        ));

        IterAsserts.empty(IteratorIter.of(
                Collections.emptyIterator()
        ));
    }

}
