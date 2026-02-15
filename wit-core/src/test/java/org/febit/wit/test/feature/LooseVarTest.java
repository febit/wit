// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.test.feature;

import org.febit.wit.EngineManager;
import org.febit.wit.Feature;
import org.febit.wit.exception.SourceNotFoundException;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

class LooseVarTest {

    @Test
    void test() throws SourceNotFoundException {

        var features = Feature.LOOSE_VAR.enable(Feature.collectFeatureDefaults());

        var engine = spy(EngineManager.engine());
        when(engine.features()).thenReturn(features);

        var script = engine.script("/feature/looseVar.wit");
        script.eval();
    }
}
