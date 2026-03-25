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
package org.febit.wit.runtime.ast.template;

import org.febit.wit.io.Out;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.ast.Position;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

import java.nio.charset.StandardCharsets;

import static org.febit.wit.io.OutTestUtils.wrapAsOutputStreamOut;
import static org.febit.wit.io.OutTestUtils.wrapAsWriterOut;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AdaptiveTemplateTextTest {

    final String hello = "Hello, World!";
    final AdaptiveTemplateText templateText = new AdaptiveTemplateText(
            hello.toCharArray(),
            hello.getBytes(StandardCharsets.UTF_8),
            StandardCharsets.UTF_8,
            mock(Position.class)
    );

    @Test
    void execute() {
        var buffer = new StringBuilder();
        var context = mock(InternalContext.class);

        buffer.setLength(0);
        when(context.out()).thenReturn(wrapAsOutputStreamOut(buffer));
        templateText.execute(context);
        assertEquals(hello, buffer.toString());

        buffer.setLength(0);
        when(context.out()).thenReturn(wrapAsWriterOut(buffer));
        templateText.execute(context);
        assertEquals(hello, buffer.toString());
    }

    @Test
    void differentCharset() {
        var out = mock(Out.class);
        when(out.preferBytes()).thenReturn(true);
        when(out.charset()).thenReturn(StandardCharsets.UTF_16);

        var context = mock(InternalContext.class);
        when(context.out()).thenReturn(out);

        templateText.execute(context);
        verify(out).write(ArgumentMatchers.isA(char[].class));
        verify(out, never()).write(ArgumentMatchers.isA(byte[].class));
    }
}
