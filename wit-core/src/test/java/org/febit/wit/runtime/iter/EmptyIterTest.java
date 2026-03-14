package org.febit.wit.runtime.iter;

import org.junit.jupiter.api.Test;

class EmptyIterTest {

    @Test
    void test() {
        var iter = new EmptyIter();
        IterAsserts.empty(iter);
    }

}
