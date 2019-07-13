// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.test.tmpls;

import org.junit.jupiter.api.Test;

import static org.febit.wit.EngineManager.tmplChecker;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * @author zqq90
 */
class AssistantSuffixesTest {

    @Test
    void test() {
        assertDoesNotThrow(tmplChecker("/assistantSuffixesTest.whtml"));
        assertDoesNotThrow(tmplChecker("/assistantSuffixesTest.wit2"));
    }
}
