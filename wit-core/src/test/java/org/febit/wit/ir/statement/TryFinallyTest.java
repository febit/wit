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

import org.febit.wit.ir.Statement;
import org.febit.wit.ir.flow.Break;
import org.febit.wit.ir.flow.Jump;
import org.febit.wit.ir.flow.JumpKind;
import org.febit.wit.ir.flow.Return;
import org.febit.wit.runtime.RuntimeContext;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.febit.wit.ir.IRTestSupport.DUMMY_POS;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

class TryFinallyTest {

    @Test
    void executesFinallyAfterBody() {
        var context = mock(RuntimeContext.class);
        var body = mock(Statement.class);
        var finalBody = mock(Statement.class);
        var statement = new TryFinally(body, finalBody, DUMMY_POS);

        assertThat(statement.execute(context)).isNull();

        var inOrder = inOrder(body, finalBody);
        inOrder.verify(body).execute(context);
        inOrder.verify(finalBody).execute(context);
        verifyNoMoreInteractions(body, finalBody);
    }

    @Test
    void executesFinallyWhenBodyThrowsAndRethrowsOriginalException() {
        var context = mock(RuntimeContext.class);
        var body = mock(Statement.class);
        var finalBody = mock(Statement.class);
        var failure = new RuntimeException("boom");
        doThrow(failure).when(body).execute(context);
        var statement = new TryFinally(body, finalBody, DUMMY_POS);

        assertThatThrownBy(() -> statement.execute(context))
                .isSameAs(failure);

        var inOrder = inOrder(body, finalBody);
        inOrder.verify(body).execute(context);
        inOrder.verify(finalBody).execute(context);
        verifyNoMoreInteractions(body, finalBody);
    }

    @Test
    void propagatesFinallyExceptionWhenBothBodyAndFinallyThrow() {
        var context = mock(RuntimeContext.class);
        var body = mock(Statement.class);
        var finalBody = mock(Statement.class);
        var bodyFailure = new RuntimeException("body boom");
        var finallyFailure = new RuntimeException("finally boom");
        doThrow(bodyFailure).when(body).execute(context);
        doThrow(finallyFailure).when(finalBody).execute(context);
        var statement = new TryFinally(body, finalBody, DUMMY_POS);

        assertThatThrownBy(() -> statement.execute(context))
                .isSameAs(finallyFailure);

        var inOrder = inOrder(body, finalBody);
        inOrder.verify(body).execute(context);
        inOrder.verify(finalBody).execute(context);
        verifyNoMoreInteractions(body, finalBody);
    }

    @Test
    void skipsFinallyWhenFinalBodyIsNull() {
        var context = mock(RuntimeContext.class);
        var body = mock(Statement.class);
        var statement = new TryFinally(body, null, DUMMY_POS);

        assertThat(statement.execute(context)).isNull();

        verify(body).execute(context);
        verifyNoMoreInteractions(body);
    }

    @Test
    void collectsJumpsFromBodyAndFinally() {
        var statement = new TryFinally(
                new Break(1, DUMMY_POS),
                new Return(null, DUMMY_POS),
                DUMMY_POS
        );
        var jumps = new ArrayList<Jump>();

        statement.collectJumps(jumps::add);

        assertThat(jumps).containsExactly(
                new Jump(1, JumpKind.BREAK, DUMMY_POS),
                new Jump(0, JumpKind.RETURN, DUMMY_POS)
        );
    }

    @Test
    void ignoresMissingOrNonJumpAwareFinallyWhenCollectingJumps() {
        var body = mock(Statement.class);
        var statement = new TryFinally(body, null, DUMMY_POS);
        var jumps = new ArrayList<Jump>();

        statement.collectJumps(jumps::add);

        assertThat(jumps).isEmpty();
    }
}
