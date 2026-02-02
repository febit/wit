// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.test.feature;

import org.febit.wit.EngineManager;
import org.febit.wit.Feature;
import org.febit.wit.Vars;
import org.febit.wit.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

class ShareRootParamsTest {

    @Test
    void test() throws ResourceNotFoundException {
        var engine = spy(EngineManager.engine());
        when(engine.isEnabled(Feature.SHARE_ROOT_PARAMS))
                .thenReturn(true);

        var template = engine.template("/feature/shareRootParams.wit");
        template.reload();
        template.merge(Vars.of(Map.of(
                "v1", "V1",
                "v2", "V2"
        )));

    }
}
