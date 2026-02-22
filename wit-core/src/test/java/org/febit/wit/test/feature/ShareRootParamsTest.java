// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.test.feature;

import org.febit.wit.Feature;
import org.febit.wit.TestWit;
import org.febit.wit.Vars;
import org.febit.wit.exception.NoSuchSourceException;
import org.febit.wit.io.DiscardOut;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

class ShareRootParamsTest {

    @Test
    void test() throws NoSuchSourceException {
        var wit = spy(TestWit.WIT());
        when(wit.isEnabled(Feature.SHARE_ROOT_PARAMS))
                .thenReturn(true);

        var script = wit.script("/feature/shareRootParams.wit");
        script.reload();
        script.eval(Vars.of(Map.of(
                "v1", "V1",
                "v2", "V2"
        )), DiscardOut.get());

    }
}
