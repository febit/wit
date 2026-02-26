// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime;

import lombok.experimental.Accessors;
import org.febit.wit.Context;
import org.febit.wit.ExportedFunction;
import org.febit.wit.Feature;
import org.febit.wit.Out;
import org.febit.wit.Script;
import org.febit.wit.Vars;
import org.febit.wit.Wit;
import org.febit.wit.exception.NoSuchFunctionException;
import org.febit.wit.exception.ScriptEvaluateException;
import org.febit.wit.runtime.accessor.AccessorFactory;
import org.febit.wit.runtime.accessor.Getter;
import org.febit.wit.runtime.accessor.Render;
import org.febit.wit.runtime.accessor.Setter;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.ast.Statement;
import org.febit.wit.runtime.heap.Heap;
import org.febit.wit.runtime.heap.VariableHeap;
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
    private final VariableHeap variables;

    @lombok.Getter
    private final Heap local;

    /**
     * Output, stream or writer.
     */
    @lombok.Getter
    private Out out;

    public InternalContext(
            Script script,
            VariableHeap variables,
            Vars inputs,
            Out out,
            Heap local,
            @Nullable BreakpointHandler breakpointHandler
    ) {
        this.script = script;
        this.variables = variables;
        this.inputs = inputs;
        this.out = out;
        this.local = local;
        this.breakpointHandler = breakpointHandler;

        var wit = script.engine();
        this.features = wit.features();
        this.accessors = wit.accessors();

        inputs.sink(variables::set);
    }

    public boolean isEnabled(Feature feature) {
        return feature.isEnabled(this.features);
    }

    public Wit engine() {
        return this.script.engine();
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
        while (i < len && lp.isNone()) {
            stats[i++].execute(this);
        }
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

    @Nullable
    public Object redirect(
            Writer writer, java.util.function.Function<InternalContext, @Nullable Object> action) {
        var target = engine().asOut(writer, this.out.charset());
        return redirect(target, action);
    }

    @Nullable
    public Object redirect(
            OutputStream output, java.util.function.Function<InternalContext, @Nullable Object> action) {
        var target = engine().asOut(output, this.out.charset());
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
    public ExportedFunction exportFunction(String name) throws NoSuchFunctionException {
        var obj = this.variables().get(name, false);
        if (!(obj instanceof Function func)) {
            throw new NoSuchFunctionException(obj);
        }
        return new ExportedFunction(this.script, func, this.out.charset(), this.out.preferBytes());
    }
}
