// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.test.feature;

import org.junit.jupiter.api.Test;

import static org.febit.wit.TestWit.tmplChecker;
import static org.junit.jupiter.api.Assertions.*;

class AssistantSuffixesTest {

    @Test
    void test() {
        assertDoesNotThrow(tmplChecker("/assistantSuffixesTest.whtml"));
        assertDoesNotThrow(tmplChecker("/assistantSuffixesTest.wit2"));
    }
}
