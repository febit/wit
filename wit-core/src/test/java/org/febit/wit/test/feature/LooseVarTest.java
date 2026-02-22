// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.test.feature;

import org.febit.wit.Feature;
import org.febit.wit.TestWit;
import org.febit.wit.exception.NoSuchSourceException;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

class LooseVarTest {

    @Test
    void test() throws NoSuchSourceException {

        var features = Feature.LOOSE_VAR.enable(Feature.collectFeatureDefaults());

        var wit = spy(TestWit.WIT());
        when(wit.features()).thenReturn(features);

        var script = wit.script("/feature/looseVar.wit");
        script.eval();
    }
}
