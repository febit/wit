// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime;

import lombok.experimental.Accessors;
import org.febit.wit.Context;
import org.febit.wit.Engine;
import org.febit.wit.Feature;
import org.febit.wit.Function;
import org.febit.wit.Out;
import org.febit.wit.Script;
import org.febit.wit.Vars;
import org.febit.wit.exception.NotFunctionException;
import org.febit.wit.exception.ParseException;
import org.febit.wit.exception.ScriptEvaluateException;
import org.febit.wit.exception.SourceNotFoundException;
import org.febit.wit.io.OutputStreamOut;
import org.febit.wit.io.WriterOut;
import org.febit.wit.runtime.accessor.AccessorFactory;
import org.febit.wit.runtime.accessor.Getter;
import org.febit.wit.runtime.accessor.Render;
import org.febit.wit.runtime.accessor.Setter;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.ast.FrameIndexer;
import org.febit.wit.runtime.ast.Statement;
import org.febit.wit.runtime.function.FunctionDeclare;
import org.febit.wit.runtime.heap.Heap;
import org.febit.wit.runtime.heap.VariantHeap;
import org.jspecify.annotations.Nullable;

import java.io.OutputStream;
import java.io.Writer;

/**
 * Internal Context.
 * <p>
 * store variables and access global components for AST-nodes
 *
 */
@Accessors(fluent = true)
@SuppressWarnings({
        "squid:RedundantThrowsDeclarationCheck"
})
public final class InternalContext implements Context {

    @lombok.Getter
    private final Script script;

    private final int features;

    @Nullable
    @lombok.Getter
    private final BreakpointHandler breakpointHandler;

    private final AccessorFactory accessors;

    @lombok.Getter
    private final Loop loop = new Loop();

    /**
     * Input parameters.
     */
    @lombok.Getter
    private final Vars inputs;

    @lombok.Getter
    private final VariantHeap heap;

    @lombok.Getter
    private final Heap local;

    /**
     * Output, stream or writer.
     */
    @lombok.Getter
    private Out out;

    public InternalContext(
            Script script,
            Out out,
            Vars inputs,
            VariantHeap heap,
            Heap local,
            @Nullable BreakpointHandler breakpointHandler
    ) {
        this.script = script;
        this.inputs = inputs;
        this.out = out;
        this.heap = heap;
        this.local = local;

        var engine = script.engine();
        this.features = engine.features();
        this.accessors = engine.accessors();

        this.breakpointHandler = breakpointHandler;
        //import params
        inputs.sink(heap::set);
    }

    public void handleBreakpoint(@Nullable Object label, Statement statement, @Nullable Object result) {
        if (this.breakpointHandler != null) {
            this.breakpointHandler.handle(label, this, statement, result);
        }
    }

    public Context mergeScript(String refer, String path, Vars vars)
            throws SourceNotFoundException, ScriptEvaluateException, ParseException {
        var tmpl = this.script.engine().script(refer, path);
        return tmpl.merge(this, vars);
    }

    public Object[] visit(Expression[] exprs) {
        var len = exprs.length;
        var results = new Object[len];
        for (int i = 0; i < len; i++) {
            results[i] = exprs[i].execute(this);
        }
        return results;
    }

    public void visit(Statement[] stats) {
        var i = 0;
        var len = stats.length;
        while (i < len) {
            stats[i++].execute(this);
        }
    }

    public void visitAndCheckLoop(Statement[] stats) {
        var i = 0;
        var len = stats.length;
        var lp = this.loop();
        while (i < len && lp.isNoop()) {
            stats[i++].execute(this);
        }
    }

    /**
     * Create a sub-context used by function call.
     *
     * @param callerContext local context
     * @param indexers      indexers
     * @param frameSize     var size
     * @return a new sub context
     */
    public InternalContext createSubContext(InternalContext callerContext, FrameIndexer[] indexers, int frameSize) {
        var subHeap = this.heap().shift(frameSize, indexers);
        return new InternalContext(
                script,
                callerContext.out,
                Vars.empty(),
                subHeap,
                callerContext.local(),
                breakpointHandler
        );
    }

    /**
     * Create a peer-context used by include/import.
     * <p>
     * Only share locals and out
     *
     * @return a new peer context
     */
    public InternalContext createPeerContext(Script script, VariantHeap heap, Vars inputs) {
        return new InternalContext(
                script, out(), inputs,
                heap, local(), breakpointHandler
        );
    }

    /**
     * Get a bean's property.
     *
     * @param <T>      bean type
     * @param obj      bean
     * @param property property
     * @return value
     */
    @Nullable
    public <T> Object getBeanProperty(@Nullable T obj, @Nullable Object property) {
        if (obj == null) {
            return handleAccessorNullPointer();
        }
        @SuppressWarnings("unchecked")
        var getter = (Getter<Object>) this.accessors.getter(obj.getClass());
        return getter.get(obj, property);
    }

    /**
     * Set a bean's property.
     *
     * @param obj      bean
     * @param property property
     * @param value    value
     */
    public <T> void setBeanProperty(@Nullable T obj, @Nullable Object property, @Nullable Object value) {
        if (obj == null) {
            handleAccessorNullPointer();
            return;
        }
        @SuppressWarnings("unchecked")
        var setter = (Setter<Object>) this.accessors.setter(obj.getClass());
        setter.set(obj, property, value);
    }

    @Nullable
    private Object handleAccessorNullPointer() {
        if (isEnabled(Feature.IGNORE_ACCESSOR_NULL_POINTER)) {
            return null;
        }
        throw new ScriptEvaluateException("Null pointer.");
    }

    public boolean isEnabled(Feature feature) {
        return feature.isEnabled(this.features);
    }

    @Nullable
    public Object redirect(
            Writer writer, java.util.function.Function<InternalContext, @Nullable Object> action) {
        var target = new WriterOut(writer, this.out.charset(), this.engine().codecFactory());
        return redirect(target, action);
    }

    @Nullable
    public Object redirect(
            OutputStream output, java.util.function.Function<InternalContext, @Nullable Object> action) {
        var target = new OutputStreamOut(output, this.out.charset(), this.engine().codecFactory());
        return redirect(target, action);
    }

    @Nullable
    public Object redirect(
            Out target, java.util.function.Function<InternalContext, @Nullable Object> action) {
        Out prevOut = this.out;
        this.out = target;
        try {
            return action.apply(this);
        } finally {
            this.out = prevOut;
        }
    }

    public <T> void out(@Nullable T obj) {
        if (obj == null) {
            return;
        }
        var type = obj.getClass();
        if (type == String.class) {
            out.write((String) obj);
            return;
        }
        @SuppressWarnings("unchecked")
        var render = (Render<Object>) this.accessors.render(type);
        render.render(out, obj);
    }

    @Override
    public Function exportFunction(String name) throws NotFunctionException {
        var obj = this.heap().get(name, false);
        if (!(obj instanceof FunctionDeclare func)) {
            throw new NotFunctionException(obj);
        }
        return new Function(this.script, func, this.out.charset(), this.out.preferBytes());
    }

    public Engine engine() {
        return this.script.engine();
    }
}
