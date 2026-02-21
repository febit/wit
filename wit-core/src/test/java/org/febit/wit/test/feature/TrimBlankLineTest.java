// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.test.feature;

import org.febit.wit.Feature;
import org.febit.wit.TestWit;
import org.febit.wit.Vars;
import org.febit.wit.exception.SourceNotFoundException;
import org.febit.wit.io.DiscardOut;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

class TrimBlankLineTest {

    @Test
    void test() throws SourceNotFoundException {

        var wit = spy(TestWit.WIT());
        when(wit.features())
                .thenReturn(Feature.TRIM_CODE_BLOCK_BLANK_LINE.enable(Feature.collectFeatureDefaults()));

        var script = wit.script("/feature/trimBlankLine.wit");
        script.eval(Vars.of(Map.of(
                "trimBlankLine", true
        )), DiscardOut.get());

        when(wit.features())
                .thenReturn(Feature.TRIM_CODE_BLOCK_BLANK_LINE.disable(Feature.collectFeatureDefaults()));
        script.reset();

        script.eval(Vars.of(Map.of(
                "trimBlankLine", false
        )), DiscardOut.get());
    }
}
