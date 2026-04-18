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
package org.febit.wit.ir.statement;

import org.febit.wit.engine.BreakpointHandler;
import org.febit.wit.ir.Statement;
import org.febit.wit.runtime.RuntimeContext;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.febit.wit.ir.IRTestSupport.DUMMY_POS;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class BreakpointStatementTest {

    @Test
    void executesSupervisedStatementBeforeInvokingHandler() {
        var context = mock(RuntimeContext.class);
        var handler = mock(BreakpointHandler.class);
        var supervised = mock(Statement.class);
        when(context.breakpointHandler()).thenReturn(handler);

        var statement = new BreakpointStatement("mark", supervised, DUMMY_POS);

        assertThat(statement.execute(context)).isNull();

        var inOrder = inOrder(supervised, handler);
        inOrder.verify(supervised).execute(context);
        inOrder.verify(handler).handle("mark", context, statement, null);
        verifyNoMoreInteractions(supervised, handler);
    }

    @Test
    void invokesHandlerEvenWhenSupervisedStatementIsNull() {
        var context = mock(RuntimeContext.class);
        var handler = mock(BreakpointHandler.class);
        when(context.breakpointHandler()).thenReturn(handler);

        var statement = new BreakpointStatement("mark", null, DUMMY_POS);

        assertThat(statement.execute(context)).isNull();

        verify(handler).handle("mark", context, statement, null);
        verifyNoMoreInteractions(handler);
    }

    @Test
    void executesSupervisedStatementSafelyWhenHandlerIsNull() {
        var context = mock(RuntimeContext.class);
        var supervised = mock(Statement.class);
        when(context.breakpointHandler()).thenReturn(null);

        var statement = new BreakpointStatement(null, supervised, DUMMY_POS);

        assertThat(statement.execute(context)).isNull();

        verify(supervised).execute(context);
        verify(context).breakpointHandler();
        verifyNoMoreInteractions(supervised, context);
    }

    @Test
    void doesNothingExtraWhenBothSupervisedAndHandlerAreNull() {
        var context = mock(RuntimeContext.class);
        when(context.breakpointHandler()).thenReturn(null);

        var statement = new BreakpointStatement(null, null, DUMMY_POS);

        assertThat(statement.execute(context)).isNull();

        verify(context).breakpointHandler();
        verifyNoMoreInteractions(context);
    }
}
