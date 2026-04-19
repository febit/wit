/*
 * Copyright 2013-present febit.org (support@febit.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.febit.wit.script.feature;

import org.febit.wit.Feature;
import org.febit.wit.Vars;
import org.febit.wit.exception.NoSuchSourceException;
import org.febit.wit.io.out.DiscardOut;
import org.febit.wit.script.WitTestSupport;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

class TrimBlankLineTest {

    @Test
    void test() throws NoSuchSourceException {

        var wit = spy(WitTestSupport.WIT());
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
