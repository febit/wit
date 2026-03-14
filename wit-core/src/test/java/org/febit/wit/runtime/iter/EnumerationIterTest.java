package org.febit.wit.runtime.iter;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Vector;

class EnumerationIterTest {

    @Test
    void test() {
        IterAsserts.abc(EnumerationIter.of(new Vector<String>(
                List.of("a", "b", "c")
        ).elements()));

        IterAsserts.empty(EnumerationIter.of(new Vector<String>().elements()));
    }
}
