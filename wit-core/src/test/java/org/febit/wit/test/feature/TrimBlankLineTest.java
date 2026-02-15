// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.test.feature;

import org.febit.wit.EngineManager;
import org.febit.wit.Feature;
import org.febit.wit.Vars;
import org.febit.wit.exceptions.SourceNotFoundException;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

class TrimBlankLineTest {

    @Test
    void test() throws SourceNotFoundException {
        var engine = spy(EngineManager.engine());

        when(engine.isEnabled(Feature.TRIM_CODE_BLOCK_BLANK_LINE))
                .thenReturn(true);
        var script = engine.script("/feature/trimBlankLine.wit");
        script.merge(Vars.of(Map.of(
                "trimBlankLine", true
        )));

        when(engine.isEnabled(Feature.TRIM_CODE_BLOCK_BLANK_LINE))
                .thenReturn(false);
        script.reset();

        script.merge(Vars.of(Map.of(
                "trimBlankLine", false
        )));
    }
}
