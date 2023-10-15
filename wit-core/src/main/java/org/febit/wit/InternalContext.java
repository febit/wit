// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit;

import jakarta.annotation.Nullable;
import lombok.Getter;
import org.febit.wit.lang.BreakpointListener;
import org.febit.wit.exceptions.NotFunctionException;
import org.febit.wit.exceptions.ParseException;
import org.febit.wit.exceptions.ResourceNotFoundException;
import org.febit.wit.exceptions.ScriptRuntimeException;
import org.febit.wit.io.Out;
import org.febit.wit.lang.InternedEncoding;
import org.febit.wit.lang.LoopMeta;
import org.febit.wit.lang.MethodDeclare;
import org.febit.wit.lang.VariantIndexer;
import org.febit.wit.lang.ast.Expression;
import org.febit.wit.lang.ast.Statement;
import org.febit.wit.resolvers.GetResolver;
import org.febit.wit.resolvers.OutResolver;
import org.febit.wit.resolvers.ResolverManager;
import org.febit.wit.resolvers.SetResolver;
import org.febit.wit.util.ClassMap;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Internal Context.
 * <p>
 * store variables and access global components for AST-nodes
 *
 * @author zqq90
 */
@SuppressWarnings({
        "squid:RedundantThrowsDeclarationCheck"
})
public final class InternalContext implements Context {

    @Getter
    private final Template template;

    @Nullable
    private final BreakpointListener breakpointListener;

    /**
     * params for this context.
     */
    @Getter
    private final Vars rootParams;

    /**
     * Variables in this scope.
     */
    public final Object[] vars;
    /**
     * Parent scopes's variables, if this's a sub-context.
     */
    private final Object[][] parentScopes;
    /**
     * Variables indexers.
     */
    private final VariantIndexer[] indexers;
    /**
     * If this.write is prefer bytes.
     */
    public final boolean preferBytes;
    /**
     * Output's charset.
     */
    public final InternedEncoding encoding;
    /**
     * Index of current indexer.
     */
    @SuppressWarnings({
            "squid:ClassVariableVisibilityCheck"
    })
    public int indexer;

    /**
     * Output, stream or writer.
     */
    private Out out;

    /**
     * Used by functions, store value to be returned.
     */
    private Object returned;
    /**
     * Current goto label, if looped.
     */
    private int label;
    /**
     * Current loop type, ==0 if no loop.
     */
    @Getter
    private int loopType;

    /**
     * Store local variables, only the root context need this.
     */
    private Map<Object, Object> locals;
    /**
     * context to get locals, may not the root context.
     */
    private InternalContext localContext;

    private final ResolverManager resolverManager;
    private final ClassMap<OutResolver> outers;
    private final ClassMap<GetResolver> getters;
    private final ClassMap<SetResolver> setters;

    public InternalContext(
            final Template template,
            final Out out,
            final Vars rootParams,
            final VariantIndexer[] indexers,
            final int varSize,
            final Object[][] parentScopes,
            @Nullable BreakpointListener breakpointListener
    ) {
        this.template = template;
        this.rootParams = rootParams;

        //output
        this.out = out;
        this.encoding = out.getEncoding();
        this.preferBytes = out.preferBytes();

        //resolvers
        var resolverMgr = template.getEngine().getResolverManager();
        this.resolverManager = resolverMgr;
        this.outers = resolverMgr.outers;
        this.getters = resolverMgr.getters;
        this.setters = resolverMgr.setters;

        //variables & indexers
        this.indexers = indexers;
        this.indexer = 0;
        this.vars = new Object[varSize];
        this.parentScopes = parentScopes;

        this.breakpointListener = breakpointListener;
        //import params
        rootParams.exportTo(this::set);
    }

    public void onBreakpoint(@Nullable Object label, Statement statement, @Nullable Object result) {
        if (this.breakpointListener != null) {
            this.breakpointListener.onBreakpoint(label, this, statement, result);
        }
    }

    public Context mergeTemplate(String refer, String path, Vars vars)
            throws ResourceNotFoundException, ScriptRuntimeException, ParseException {
        var template = getEngine().getTemplate(refer, path);
        return template.mergeToContext(this, vars);
    }

    public Object[] execute(Expression[] exprs) {
        var len = exprs.length;
        var results = new Object[len];
        for (int i = 0; i < len; i++) {
            results[i] = exprs[i].execute(this);
        }
        return results;
    }

    public void execute(final Statement[] stats) {
        var i = 0;
        var len = stats.length;
        while (i < len) {
            stats[i++].execute(this);
        }
    }

    public void executeWithLoop(final Statement[] stats) {
        var i = 0;
        var len = stats.length;
        while (i < len && noLoop()) {
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
        Object[][] scopes;
        if (myParentScopes == null) {
            scopes = new Object[][]{this.vars};
        } else {
            scopes = new Object[myParentScopes.length + 1][];
            scopes[0] = this.vars;
            System.arraycopy(myParentScopes, 0, scopes, 1, myParentScopes.length);
        }

        var newContext = new InternalContext(template, localContext.out, Vars.EMPTY,
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
        this.loopType = LoopMeta.BREAK;
    }

    /**
     * Mark a continue-loop.
     *
     * @param label label id
     */
    public void continueLoop(int label) {
        this.label = label;
        this.loopType = LoopMeta.CONTINUE;
    }

    /**
     * Mark a return-loop.
     *
     * @param value the returned.
     */
    public void returnLoop(Object value) {
        this.returned = value;
        this.label = 0;
        this.loopType = LoopMeta.RETURN;
    }

    /**
     * Unmark loops.
     */
    public void resetLoop() {
        this.returned = null;
        this.label = 0;
        this.loopType = 0;
    }

    /**
     * Unmark loops, is a break and match the label.
     *
     * @param label label id
     */
    public void resetBreakLoopIfMatch(int label) {
        if (this.loopType == LoopMeta.BREAK
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
        var result = this.loopType == LoopMeta.RETURN
                ? this.returned
                : VOID;
        resetLoop();
        return result;
    }

    public boolean noLoop() {
        return this.loopType == 0;
    }

    /**
     * Get a bean's property.
     *
     * @param <T>      bean type
     * @param bean     bean
     * @param property property
     * @return value
     */
    public <T> Object getBeanProperty(final T bean, final Object property) {
        if (bean != null) {
            @SuppressWarnings("unchecked")
            GetResolver<T> resolver = this.getters.unsafeGet(bean.getClass());
            if (resolver != null) {
                return resolver.get(bean, property);
            }
        }
        return this.resolverManager.get(bean, property);
    }

    /**
     * Set a bean's property.
     *
     * @param bean     bean
     * @param property property
     * @param value    value
     */
    public <T> void setBeanProperty(final T bean, final Object property, final Object value) {
        if (bean != null) {
            @SuppressWarnings("unchecked")
            SetResolver<T> resolver = this.setters.unsafeGet(bean.getClass());
            if (resolver != null) {
                resolver.set(bean, property, value);
                return;
            }
        }
        this.resolverManager.set(bean, property, value);
    }

    public void outNotNull(final byte[] bytes) {
        this.out.write(bytes);
    }

    public void outNotNull(final char[] chars) {
        this.out.write(chars);
    }

    public Object temporaryOut(Out newOut, java.util.function.Function<InternalContext, Object> action) {
        Out prevOut = this.out;
        this.out = newOut;
        try {
            return action.apply(this);
        } finally {
            this.out = prevOut;
        }
    }

    @SuppressWarnings("unchecked")
    public <T> void write(final T obj) {
        if (obj == null) {
            return;
        }
        var type = obj.getClass();
        if (type == String.class) {
            this.out.write((String) obj);
            return;
        }
        var resolver = this.outers.unsafeGet(type);
        if (resolver != null) {
            resolver.render(this.out, obj);
            return;
        }
        this.resolverManager.resolveOutResolver(type)
                .render(this.out, obj);
    }

    @Override
    public Object getLocal(final Object name) {
        if (localContext != null) {
            return localContext.getLocal(name);
        }
        var map = this.locals;
        return map != null ? map.get(name) : null;
    }

    @Override
    public void setLocal(final Object name, final Object value) {
        if (this.localContext != null) {
            this.localContext.setLocal(name, value);
            return;
        }
        if (this.locals == null) {
            this.locals = new HashMap<>(16);
        }
        this.locals.put(name, value);
    }

    @Override
    public void set(final String name, final Object value) {
        int index = this.indexers[this.indexer].getIndex(name);
        if (index >= 0) {
            this.vars[index] = value;
        }
    }

    @Override
    public Object get(final String name) throws ScriptRuntimeException {
        return get(name, true);
    }

    @Override
    public Object get(final String name, boolean force) throws ScriptRuntimeException {
        int index = getCurrentIndexer().getIndex(name);
        if (index >= 0) {
            return this.vars[index];
        }
        if (force) {
            throw new ScriptRuntimeException("Not found variant named:".concat(name));
        }
        return null;
    }

    public Object getParentScopeValue(int scope, int index) {
        return this.parentScopes[scope][index];
    }

    public void setParentScopeValue(int scope, int index, Object value) {
        this.parentScopes[scope][index] = value;
    }

    public VariantIndexer getCurrentIndexer() {
        return this.indexers[this.indexer];
    }

    @Override
    public void forEachVar(BiConsumer<? super String, Object> action) {
        var myVars = this.vars;
        getCurrentIndexer()
                .forEach((name, index)
                        -> action.accept(name, myVars[index]));
    }

    @Override
    public void exportTo(final Map<? super String, Object> map) {
        forEachVar(map::put);
    }

    @Override
    public Function exportFunction(String name) throws NotFunctionException {
        var func = get(name, false);
        if (!(func instanceof MethodDeclare)) {
            throw new NotFunctionException(func);
        }
        return new Function(this.template, (MethodDeclare) func, this.encoding, this.preferBytes);
    }

    public Engine getEngine() {
        return this.template.getEngine();
    }
}
