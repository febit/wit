// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit;

import lombok.experimental.Accessors;
import org.febit.wit.accessor.AccessorFactory;
import org.febit.wit.accessor.Getter;
import org.febit.wit.accessor.Render;
import org.febit.wit.accessor.Setter;
import org.febit.wit.exceptions.NotFunctionException;
import org.febit.wit.exceptions.ParseException;
import org.febit.wit.exceptions.ResourceNotFoundException;
import org.febit.wit.exceptions.ScriptRuntimeException;
import org.febit.wit.lang.BreakpointListener;
import org.febit.wit.lang.FunctionDeclare;
import org.febit.wit.lang.LoopMeta;
import org.febit.wit.lang.Out;
import org.febit.wit.lang.VariantIndexer;
import org.febit.wit.lang.ast.Expression;
import org.febit.wit.lang.ast.Statement;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

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
    private final Template template;

    private final int features;

    @Nullable
    private final BreakpointListener breakpointListener;

    /**
     * params for this context.
     */
    @lombok.Getter
    private final Vars rootParams;

    /**
     * Variables in this scope.
     */
    public final @Nullable Object[] vars;
    /**
     * Parent scopes's variables, if this is a sub-context.
     */
    private final @Nullable Object @Nullable [][] parentScopes;
    /**
     * Variables indexers.
     */
    private final VariantIndexer[] indexers;
    /**
     * Index of current indexer.
     */
    private int indexer;

    /**
     * Output, stream or writer.
     */
    @lombok.Getter
    private Out out;

    /**
     * Used by functions, store value to be returned.
     */
    @Nullable
    private Object returned;
    /**
     * Current goto label, if looped.
     */
    private int label;
    /**
     * Current loop kind, ==0 if no loop.
     */
    @lombok.Getter
    private LoopMeta.Kind loopKind = LoopMeta.Kind.NOOP;

    /**
     * Store local variables, only the root context need this.
     */
    @Nullable
    private Map<Object, @Nullable Object> locals;
    /**
     * context to get locals, may not the root context.
     */
    @Nullable
    private InternalContext localContext;

    private final AccessorFactory accessors;

    public InternalContext(
            final Template template,
            final Out out,
            final Vars rootParams,
            final VariantIndexer[] indexers,
            final int varSize,
            @Nullable Object @Nullable [][] parentScopes,
            @Nullable BreakpointListener breakpointListener
    ) {
        this.template = template;
        this.rootParams = rootParams;
        this.out = out;

        var engine = template.engine();
        this.features = engine.features();
        this.accessors = engine.accessors();

        //variables & indexers
        this.indexers = indexers;
        this.indexer = 0;
        this.vars = new Object[varSize];
        this.parentScopes = parentScopes;

        this.breakpointListener = breakpointListener;
        //import params
        rootParams.sink(this::setVar);
    }

    public void handleBreakpoint(@Nullable Object label, Statement statement, @Nullable Object result) {
        if (this.breakpointListener != null) {
            this.breakpointListener.onBreakpoint(label, this, statement, result);
        }
    }

    public Context mergeTemplate(String refer, String path, Vars vars)
            throws ResourceNotFoundException, ScriptRuntimeException, ParseException {
        var tmpl = this.template.engine().template(refer, path);
        return tmpl.mergeToContext(this, vars);
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
        while (i < len && this.loopKind().isNoop()) {
            stats[i++].execute(this);
        }
    }

    /**
     * Create a sub context.
     *
     * @param indexers     indexers
     * @param localContext local context
     * @param varSize      var size
     * @return a new sub context
     */
    public InternalContext createSubContext(VariantIndexer[] indexers, InternalContext localContext, int varSize) {
        var myParentScopes = this.parentScopes;
        //cal the new-context's parent-scopes
        @Nullable Object[][] scopes;
        if (myParentScopes == null) {
            scopes = new Object[][]{this.vars};
        } else {
            scopes = new Object[myParentScopes.length + 1][];
            scopes[0] = this.vars;
            System.arraycopy(myParentScopes, 0, scopes, 1, myParentScopes.length);
        }

        var newContext = new InternalContext(template, localContext.out, Vars.empty(),
                indexers, varSize, scopes, breakpointListener);
        newContext.localContext = localContext;
        return newContext;
    }

    /**
     * Create a peer-context used by include/import.
     * <p>
     * Only share locals and out
     *
     * @param template   template
     * @param indexers   indexers
     * @param varSize    var size
     * @param rootParams root params
     * @return a new peer context
     */
    public InternalContext createPeerContext(Template template, VariantIndexer[] indexers, int varSize, Vars rootParams) {
        var newContext = new InternalContext(template, this.out, rootParams,
                indexers, varSize, null, breakpointListener);
        newContext.localContext = this;
        return newContext;
    }

    /**
     * if gaven loop label matched current loop.
     *
     * @param label label id
     * @return true if match
     */
    @SuppressWarnings({
            "BooleanMethodIsAlwaysInverted"
    })
    public boolean matchLabel(int label) {
        return this.label == 0 || this.label == label;
    }

    /**
     * Mark a break-loop.
     *
     * @param label label id
     */
    public void breakLoop(int label) {
        this.label = label;
        this.loopKind = LoopMeta.Kind.BREAK;
    }

    /**
     * Mark a continue-loop.
     *
     * @param label label id
     */
    public void continueLoop(int label) {
        this.label = label;
        this.loopKind = LoopMeta.Kind.CONTINUE;
    }

    /**
     * Mark a return-loop.
     *
     * @param value the returned.
     */
    public void returnLoop(@Nullable Object value) {
        this.returned = value;
        this.label = 0;
        this.loopKind = LoopMeta.Kind.RETURN;
    }

    /**
     * Unmark loops.
     */
    public void resetLoop() {
        this.returned = null;
        this.label = 0;
        this.loopKind = LoopMeta.Kind.NOOP;
    }

    /**
     * Unmark loops, is a break and match the label.
     *
     * @param label label id
     */
    public void resetBreakLoopIfMatch(int label) {
        if (this.loopKind.isBreak()
                && (this.label == 0 || this.label == label)) {
            this.resetLoop();
        }
    }

    /**
     * Unmark loops, at the end of functions.
     *
     * @return the returned
     */
    @Nullable
    public Object resetReturnLoop() {
        var result = this.loopKind.isReturn()
                ? this.returned
                : VOID;
        resetLoop();
        return result;
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
        throw new ScriptRuntimeException("Null pointer.");
    }

    public boolean isEnabled(Feature feature) {
        return feature.isEnabled(this.features);
    }

    @Nullable
    public Object redirectOut(Out newOut, java.util.function.Function<InternalContext, @Nullable Object> action) {
        Out prevOut = this.out;
        this.out = newOut;
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

    @Nullable
    @Override
    public Object getLocalVar(Object name) {
        if (localContext != null) {
            return localContext.getLocalVar(name);
        }
        var map = this.locals;
        return map != null ? map.get(name) : null;
    }

    @Override
    public void setLocalVar(Object name, @Nullable Object value) {
        if (this.localContext != null) {
            this.localContext.setLocalVar(name, value);
            return;
        }
        if (this.locals == null) {
            this.locals = new HashMap<>(16);
        }
        this.locals.put(name, value);
    }

    @Nullable
    public <T extends @Nullable Object> T pushIndexer(
            int indexer,
            java.util.function.Function<InternalContext, T> action
    ) {
        var prev = this.indexer;
        this.indexer = indexer;
        try {
            return action.apply(this);
        } finally {
            this.indexer = prev;
        }
    }

    @Override
    public void setVar(String name, @Nullable Object value) {
        int index = this.indexers[this.indexer].getIndex(name);
        if (index >= 0) {
            this.vars[index] = value;
        }
    }

    @Nullable
    @Override
    public Object getVar(String name) throws ScriptRuntimeException {
        return getVar(name, true);
    }

    @Nullable
    @Override
    public Object getVar(String name, boolean force) throws ScriptRuntimeException {
        int index = getCurrentIndexer().getIndex(name);
        if (index >= 0) {
            return this.vars[index];
        }
        if (force) {
            throw new ScriptRuntimeException("Not found variant named:" + name);
        }
        return null;
    }

    @Nullable
    public Object getParentScopeValue(int scope, int index) {
        var scopes = this.parentScopes;
        if (scopes == null) {
            throw new IllegalStateException("No parent scopes.");
        }
        return scopes[scope][index];
    }

    public void setParentScopeValue(int scope, int index, @Nullable Object value) {
        var scopes = this.parentScopes;
        if (scopes == null) {
            throw new IllegalStateException("No parent scopes.");
        }
        scopes[scope][index] = value;
    }

    public VariantIndexer getCurrentIndexer() {
        return this.indexers[this.indexer];
    }

    @Override
    public void forEachVar(BiConsumer<? super String, @Nullable Object> action) {
        var myVars = this.vars;
        getCurrentIndexer().forEach(
                (name, index) -> action.accept(name, myVars[index])
        );
    }

    @Override
    public void exportVars(Map<? super String, @Nullable Object> map) {
        forEachVar(map::put);
    }

    @Override
    public Function exportFunction(String name) throws NotFunctionException {
        var func = getVar(name, false);
        if (!(func instanceof FunctionDeclare)) {
            throw new NotFunctionException(func);
        }
        return new Function(this.template, (FunctionDeclare) func, this.out.charset(), this.out.preferBytes());
    }

    public Engine engine() {
        return this.template.engine();
    }
}
