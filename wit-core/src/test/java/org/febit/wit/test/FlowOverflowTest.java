// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.test;

import org.febit.wit.exception.ParseException;
import org.junit.jupiter.api.Test;

import static org.febit.wit.TestWit.tmplChecker;
import static org.junit.jupiter.api.Assertions.*;

class FlowOverflowTest {

    @Test
    void test() {
        assertThrows(ParseException.class,
                tmplChecker("/flow-overflow/break-in-function.wit"));

        assertThrows(ParseException.class,
                tmplChecker("/flow-overflow/label-mismatch.wit"));

        assertThrows(ParseException.class,
                tmplChecker("/flow-overflow/switch-continue.wit"));
    }
}
