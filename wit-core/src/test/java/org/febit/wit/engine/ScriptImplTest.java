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
package org.febit.wit.engine;

import org.febit.wit.Vars;
import org.febit.wit.Wit;
import org.febit.wit.engine.accessor.AccessorFactory;
import org.febit.wit.exception.ScriptEvaluateException;
import org.febit.wit.io.Out;
import org.febit.wit.io.Source;
import org.febit.wit.io.out.DiscardOut;
import org.febit.wit.ir.ScriptIR;
import org.febit.wit.parser.Parser;
import org.febit.wit.runtime.RuntimeContext;
import org.febit.wit.runtime.heap.GenericHeap;
import org.febit.wit.runtime.heap.VariableHeap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ScriptImplTest {

    private static final String PATH = "/test.wit";

    private final AccessorFactory accessors = mock(AccessorFactory.class);
    private final ParserFactory parserFactory = mock(ParserFactory.class);
    private final Parser parser = mock(Parser.class);
    private final Source source = mock(Source.class);

    private Wit engine;

    @BeforeEach
    void setUp() {
        this.engine = mock(Wit.class);
        when(engine.parserFactory()).thenReturn(parserFactory);
        when(engine.features()).thenReturn(0);
        when(engine.accessors()).thenReturn(accessors);
        when(parserFactory.get(any(ParseContext.class))).thenReturn(parser);
    }

    private static ScriptIR ir(long sourceVersion) {
        var ir = mock(ScriptIR.class);
        when(ir.sourceVersion()).thenReturn(sourceVersion);
        when(ir.createVariableHeap()).thenReturn(VariableHeap.empty());
        return ir;
    }

    private ScriptImpl script() {
        return new ScriptImpl(engine, PATH, source);
    }

    @Test
    void evalCachesCompiledIrUntilSourceVersionChanges() {
        var ir = ir(1L);
        when(source.version()).thenReturn(1L);
        when(parser.parse()).thenReturn(ir);

        var script = script();
        script.eval();
        script.eval();

        verify(parser, times(1)).parse();

        var contextCaptor = ArgumentCaptor.forClass(ParseContext.class);
        verify(parserFactory).get(contextCaptor.capture());
        var context = contextCaptor.getValue();
        assertThat(context.engine()).isSameAs(engine);
        assertThat(context.path()).isEqualTo(PATH);
        assertThat(context.source()).isSameAs(source);
    }

    @Test
    void evalReparsesWhenSourceVersionChanges() {
        var ir1 = ir(1L);
        var ir2 = ir(2L);
        when(source.version()).thenReturn(1L);
        when(parser.parse()).thenReturn(ir1, ir2);

        var script = script();
        script.eval();
        when(source.version()).thenReturn(2L);
        script.eval();

        verify(parser, times(2)).parse();
    }

    @Test
    void reloadAndResetForceReparse() {
        var ir = ir(1L);
        when(source.version()).thenReturn(1L);
        when(parser.parse()).thenReturn(ir, ir, ir);

        var script = script();
        script.eval();
        script.reload();
        script.reset();
        script.eval();

        verify(parser, times(3)).parse();
    }

    @Test
    void evalUsesDefaultRuntimeComponentsWhenArgumentsAreNull() {
        var ir = ir(1L);
        var executed = new AtomicReference<RuntimeContext>();
        doAnswer(invocation -> {
            executed.set(invocation.getArgument(0));
            return null;
        }).when(ir).execute(any(RuntimeContext.class));
        when(source.version()).thenReturn(1L);
        when(parser.parse()).thenReturn(ir);

        var script = script();
        var context = (RuntimeContext) script.eval(null, null, null, null);

        assertThat(context).isSameAs(executed.get());
        assertThat(context.script()).isSameAs(script);
        assertThat(context.out()).isSameAs(DiscardOut.get());
        assertThat(context.local()).isInstanceOf(GenericHeap.class);
        assertThat(context.breakpointHandler()).isNull();

        var inputs = new HashMap<String, Object>();
        context.inputs().sink(inputs::put);
        assertThat(inputs).isEmpty();
    }

    @Test
    void evalUsesProvidedRuntimeComponents() {
        var ir = ir(1L);
        var executed = new AtomicReference<RuntimeContext>();
        doAnswer(invocation -> {
            executed.set(invocation.getArgument(0));
            return null;
        }).when(ir).execute(any(RuntimeContext.class));
        when(source.version()).thenReturn(1L);
        when(parser.parse()).thenReturn(ir);

        var inputs = Vars.of("name", "wit");
        var out = mock(Out.class);
        var local = GenericHeap.local();
        var breakpointHandler = mock(BreakpointHandler.class);

        var script = script();
        var context = (RuntimeContext) script.eval(inputs, out, local, breakpointHandler);

        assertThat(context).isSameAs(executed.get());
        assertThat(context.inputs()).isSameAs(inputs);
        assertThat(context.out()).isSameAs(out);
        assertThat(context.local()).isSameAs(local);
        assertThat(context.breakpointHandler()).isSameAs(breakpointHandler);

        var mapped = new HashMap<String, Object>();
        context.inputs().sink(mapped::put);
        assertThat(mapped).containsEntry("name", "wit");
    }

    @Test
    void evalRethrowsScriptExceptionAndAssignsScript() {
        var ir = ir(1L);
        var failure = new ScriptEvaluateException("boom");
        doThrow(failure).when(ir).execute(any(RuntimeContext.class));
        when(source.version()).thenReturn(1L);
        when(parser.parse()).thenReturn(ir);

        var script = script();

        assertThatThrownBy(script::eval)
                .isSameAs(failure)
                .satisfies(ex -> assertThat(((ScriptEvaluateException) ex).script()).isSameAs(script));
    }

    @Test
    void evalWrapsGenericExceptionAndAssignsScript() {
        var ir = ir(1L);
        var failure = new IllegalArgumentException("boom");
        doThrow(failure).when(ir).execute(any(RuntimeContext.class));
        when(source.version()).thenReturn(1L);
        when(parser.parse()).thenReturn(ir);

        var script = script();

        assertThatThrownBy(script::eval)
                .isInstanceOf(ScriptEvaluateException.class)
                .hasCause(failure)
                .satisfies(ex -> assertThat(((ScriptEvaluateException) ex).script()).isSameAs(script));
    }

    @Test
    void equalsAndHashCodeFollowCurrentIdentityRules() {
        var source1 = mock(Source.class);
        var source2 = mock(Source.class);
        var sameEngineOtherSource = new ScriptImpl(engine, PATH, source1);
        var sameEngineSamePath = new ScriptImpl(engine, PATH, source2);
        var otherEngine = new ScriptImpl(mock(Wit.class), PATH, source1);
        var otherPath = new ScriptImpl(engine, "/other.wit", source1);

        assertThat(sameEngineOtherSource)
                .isEqualTo(sameEngineOtherSource)
                .isEqualTo(sameEngineSamePath)
                .hasSameHashCodeAs(sameEngineSamePath)
                .isNotEqualTo(otherEngine)
                .isNotEqualTo(otherPath)
                .isNotEqualTo("script");
    }
}
