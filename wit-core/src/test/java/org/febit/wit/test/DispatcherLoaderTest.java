// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.test;

import org.febit.wit.TestWit;
import org.febit.wit.exception.NoSuchSourceException;
import org.junit.jupiter.api.Test;

class DispatcherLoaderTest {

    @Test
    void test() throws NoSuchSourceException {
        TestWit.script("lib:/lib.wit").eval();
        TestWit.script("lib:sub:/lib-sub.wit").eval();
    }
}
