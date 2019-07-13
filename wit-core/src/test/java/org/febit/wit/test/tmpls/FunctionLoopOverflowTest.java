// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.test.tmpls;

import org.febit.wit.exceptions.ParseException;
import org.junit.jupiter.api.Test;

import static org.febit.wit.EngineManager.tmplChecker;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author zqq90
 */
class FunctionLoopOverflowTest {

    @Test
    void test() {
        assertThrows(ParseException.class,
                tmplChecker("/loopTests/loopOverflow1.wit"));

        assertThrows(ParseException.class,
                tmplChecker("/loopTests/loopOverflow2.wit"));

        assertThrows(ParseException.class,
                tmplChecker("/loopTests/loopOverflow3.wit"));
    }
}
