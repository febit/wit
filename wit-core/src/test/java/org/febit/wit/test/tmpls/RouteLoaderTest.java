// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.test.tmpls;

import org.febit.wit.EngineManager;
import org.febit.wit.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Test;

/**
 * @author zqq90
 */
class RouteLoaderTest {

    @Test
    void test() throws ResourceNotFoundException {
        EngineManager.getTemplate("lib:/lib.wit").merge();
        EngineManager.getTemplate("lib:sub:/lib-sub.wit").merge();
    }
}
