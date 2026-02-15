// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.test.feature;

import org.febit.wit.EngineManager;
import org.febit.wit.exception.SourceNotFoundException;
import org.junit.jupiter.api.Test;

class DispatcherLoaderTest {

    @Test
    void test() throws SourceNotFoundException {
        EngineManager.script("lib:/lib.wit").eval();
        EngineManager.script("lib:sub:/lib-sub.wit").eval();
    }
}
