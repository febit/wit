// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.test.tmpls;

import org.junit.Test;
import org.febit.wit.EngineManager;
import org.febit.wit.exceptions.ResourceNotFoundException;
import org.febit.wit.io.impl.DiscardOut;

/**
 *
 * @author zqq90
 */
public class RouteLoaderTest {

    @Test
    public void test() throws ResourceNotFoundException {

        DiscardOut out = new DiscardOut();

        EngineManager.getTemplate("lib:/lib.wit").merge(out);
        EngineManager.getTemplate("lib:sub:/lib-sub.wit").merge(out);
    }
}
