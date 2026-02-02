// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.test.feature;

import org.febit.wit.EngineManager;
import org.febit.wit.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Test;

class DispatcherLoaderTest {

    @Test
    void test() throws ResourceNotFoundException {
        EngineManager.template("lib:/lib.wit").merge();
        EngineManager.template("lib:sub:/lib-sub.wit").merge();
    }
}
