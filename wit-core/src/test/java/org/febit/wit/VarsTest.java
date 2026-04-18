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
package org.febit.wit;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.febit.wit.ir.IRTestSupport.args;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

class VarsTest {

    @Test
    void empty() {
        var mocked = mock(Vars.Acceptor.class);

        Vars.empty().sink(mocked);
        verify(mocked, never()).set(any(), any());
        verify(mocked, never()).set(anyString(), any());

        Vars.concat(Vars.empty()).sink(mocked);
        verify(mocked, never()).set(any(), any());
        verify(mocked, never()).set(anyString(), any());

        Vars.concat(
                Vars.empty(),
                Vars.empty()
        ).sink(mocked);
        verify(mocked, never()).set(any(), any());
        verify(mocked, never()).set(anyString(), any());

        Vars.concat(
                Vars.empty(),
                Vars.empty(),
                Vars.empty()
        ).sink(mocked);
        verify(mocked, never()).set(any(), any());
        verify(mocked, never()).set(anyString(), any());
    }

    @Test
    void concat() {
        var mocked = mock(Vars.Acceptor.class);

        reset(mocked);
        Vars.concat((Vars[]) null).sink(mocked);
        verify(mocked, never()).set(any(), any());
        verify(mocked, never()).set(anyString(), any());

        reset(mocked);
        Vars.concat().sink(mocked);
        verify(mocked, never()).set(any(), any());
        verify(mocked, never()).set(anyString(), any());

        reset(mocked);
        Vars.concat(
                Vars.of("a", 1),
                Vars.of("a", 2),
                Vars.empty(),
                Vars.of("b", 2),
                Vars.of("a", 3)
        ).sink(mocked);

        verify(mocked).set("a", 1);
        verify(mocked).set("a", 2);
        verify(mocked).set("a", 3);
        verify(mocked).set("b", 2);
    }

    @Test
    void of() {
        var mocked = mock(Vars.Acceptor.class);

        Vars.of("a", 1).sink(mocked);
        verify(mocked).set("a", 1);

        reset(mocked);
        Vars.of(new String[]{"a", "b"}, args(1, 2)).sink(mocked);
        verify(mocked).set("a", 1);
        verify(mocked).set("b", 2);

        reset(mocked);
        Vars.of(new String[]{"a"}, args(1, 2)).sink(mocked);
        verify(mocked).set("a", 1);

        reset(mocked);
        Vars.of(new String[]{}, args()).sink(mocked);
        verify(mocked, never()).set(any(), any());
        verify(mocked, never()).set(anyString(), any());

        reset(mocked);
        Vars.of((String[]) null, null).sink(mocked);
        verify(mocked, never()).set(any(), any());
        verify(mocked, never()).set(anyString(), any());

        reset(mocked);
        Vars.of(new String[]{}, null).sink(mocked);
        verify(mocked, never()).set(any(), any());
        verify(mocked, never()).set(anyString(), any());
    }

    @Test
    void ofMap() {
        var mocked = mock(Vars.Acceptor.class);

        reset(mocked);
        Vars.of(Map.of("a", 1, "b", 2)).sink(mocked);
        verify(mocked).set((Object) "a", 1);
        verify(mocked).set((Object) "b", 2);

        reset(mocked);
        Vars.of(Map.of()).sink(mocked);
        verify(mocked, never()).set(any(), any());
        verify(mocked, never()).set(anyString(), any());

        reset(mocked);
        Vars.of((Map<?, ?>) null).sink(mocked);
        verify(mocked, never()).set(any(), any());
        verify(mocked, never()).set(anyString(), any());
    }

}
