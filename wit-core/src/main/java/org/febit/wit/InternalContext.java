// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit;

import java.util.HashMap;
import java.util.Map;
import org.febit.wit.core.LoopInfo;
import org.febit.wit.core.VariantIndexer;
import org.febit.wit.exceptions.NotFunctionException;
import org.febit.wit.exceptions.ScriptRuntimeException;
import org.febit.wit.io.Out;
import org.febit.wit.lang.InternalVoid;
import org.febit.wit.lang.MethodDeclare;
import org.febit.wit.resolvers.GetResolver;
import org.febit.wit.resolvers.OutResolver;
import org.febit.wit.resolvers.ResolverManager;
import org.febit.wit.resolvers.SetResolver;
import org.febit.wit.util.ClassMap;
import org.febit.wit.util.InternedEncoding;

/**
 * Internal Context.
 *
 * store variables and access global components for AST-nodes
 *
 * @author zqq90
 */
public final class InternalContext implements Context {

    public final Template template;
    /**
     * params for this context.
     */
    public final Vars rootParams;

    /**
     * Variables in this scope.
     */
    public final Object[] vars;
    /**
     * Parent scopes's variables, if this's a sub-context.
     */
    public final Object[][] parentScopes;
    /**
     * Variables indexers.
     */
    public final VariantIndexer[] indexers;
    /**
     * If this.write is a bytes stream.
     */
    public final boolean isByteStream;
    /**
     * Output's charset.
     */
    public final InternedEncoding encoding;
    /**
     * Index of current indexer.
     */
    public int indexer;

    /**
     * Output, stream or writer.
     */
    public Out out;

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
    private final ClassMap<OutResolver> outters;
    private final ClassMap<GetResolver> getters;
    private final ClassMap<SetResolver> setters;

    public InternalContext(final Template template, final Out out, final Vars rootParams, final VariantIndexer[] indexers, final int varSize, final Object[][] parentScopes) {
        this.template = template;
        this.rootParams = rootParams;

        //output
        this.out = out;
        this.encoding = out.getEncoding();
        this.isByteStream = out.isByteStream();

        //resolvers
        ResolverManager resolverMgr = template.engine.getResolverManager();
        this.resolverManager = resolverMgr;
        this.outters = resolverMgr.outters;
        this.getters = resolverMgr.getters;
        this.setters = resolverMgr.setters;

        //variables & indexers
        this.indexers = indexers;
        this.indexer = 0;
        this.vars = new Object[varSize];
        this.parentScopes = parentScopes;

        //import params
        rootParams.exportTo(this);
    }

    /**
     *
     * @param indexers
     * @param localContext
     * @param varSize
     * @return
     */
    public InternalContext createSubContext(VariantIndexer[] indexers, InternalContext localContext, int varSize) {
        Object[][] myParentScopes = this.parentScopes;

        //cal the new-context's parent-scopes
        Object[][] scopes;
        if (myParentScopes == null) {
            scopes = new Object[][]{this.vars};
        } else {
            scopes = new Object[myParentScopes.length + 1][];
            scopes[0] = this.vars;
            System.arraycopy(myParentScopes, 0, scopes, 1, myParentScopes.length);
        }

        InternalContext newContext = new InternalContext(template, localContext.out, Vars.EMPTY, indexers, varSize, scopes);
        //set the gaven localContext
        newContext.localContext = localContext;
        return newContext;
    }

    /**
     * Create a peer-context used by include/import.
     *
     * Only share locals and out
     *
     * @param template
     * @param indexers
     * @param varSize
     * @return
     */
    public InternalContext createPeerContext(Template template, VariantIndexer[] indexers, int varSize) {
        InternalContext newContext = new InternalContext(template, this.out, Vars.EMPTY, indexers, varSize, null);
        newContext.localContext = this;
        return newContext;
    }

    /**
     * if gaven loop label matched current loop.
     *
     * @param label
     * @return
     */
    public boolean matchLabel(int label) {
        return this.label == 0 || this.label == label;
    }

    /**
     * Mark a break-loop.
     *
     * @param label
     */
    public void breakLoop(int label) {
        this.label = label;
        this.loopType = LoopInfo.BREAK;
    }

    /**
     * Mark a continue-loop.
     *
     * @param label
     */
    public void continueLoop(int label) {
        this.label = label;
        this.loopType = LoopInfo.CONTINUE;
    }

    /**
     * Mark a return-loop.
     *
     * @param value the returned.
     */
    public void returnLoop(Object value) {
        this.returned = value;
        this.label = 0;
        this.loopType = LoopInfo.RETURN;
    }

    /**
     * Unmark loops.
     *
     */
    public void resetLoop() {
        this.returned = null;
        this.label = 0;
        this.loopType = 0;
    }

    /**
     * Unmark loops, is a break and match the label.
     *
     * @param label
     */
    public void resetBreakLoopIfMatch(int label) {
        if (this.loopType == LoopInfo.BREAK && (this.label == 0 || this.label == label)) {
            this.resetLoop();
        }
    }

    /**
     * Unmark loops, at the end of functions.
     *
     * @return the returned
     */
    public Object resetReturnLoop() {
        Object result = this.loopType == LoopInfo.RETURN ? this.returned : InternalVoid.VOID;
        resetLoop();
        return result;
    }

    public int getLoopType() {
        return this.loopType;
    }

    public boolean noLoop() {
        return this.loopType == 0;
    }

    public boolean hasLoop() {
        return this.loopType != 0;
    }

    /**
     * Get a bean's property.
     *
     * @param bean
     * @param property
     * @return
     */
    @SuppressWarnings("unchecked")
    public Object getBeanProperty(final Object bean, final Object property) {
        if (bean != null) {
            final GetResolver resolver = this.getters.unsafeGet(bean.getClass());
            if (resolver != null) {
                return resolver.get(bean, property);
            }
        }
        return this.resolverManager.get(bean, property);
    }

    /**
     * Set a bean's property.
     *
     * @param bean
     * @param property
     * @param value
     */
    @SuppressWarnings("unchecked")
    public void setBeanProperty(final Object bean, final Object property, final Object value) {
        if (bean != null) {
            final SetResolver resolver = this.setters.unsafeGet(bean.getClass());
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

    @SuppressWarnings("unchecked")
    public void write(final Object obj) {
        if (obj == null) {
            return;
        }
        final Class type = obj.getClass();
        if (type == String.class) {
            this.out.write((String) obj);
            return;
        }
        final OutResolver resolver = this.outters.unsafeGet(type);
        if (resolver != null) {
            resolver.render(this.out, obj);
            return;
        }
        this.resolverManager.resolveOutResolver(type).render(this.out, obj);
    }

    @Override
    public Object getLocal(final Object name) {
        if (localContext != null) {
            return localContext.getLocal(name);
        }
        final Map<Object, Object> map = this.locals;
        return map != null ? map.get(name) : null;
    }

    @Override
    public void setLocal(final Object name, final Object value) {
        if (this.localContext != null) {
            this.localContext.setLocal(name, value);
            return;
        }
        if (this.locals == null) {
            this.locals = new HashMap<>();
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
        int index = indexers[this.indexer].getIndex(name);
        if (index >= 0) {
            return this.vars[index];
        }
        if (force) {
            throw new ScriptRuntimeException("Not found variant named:".concat(name));
        }
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void exportTo(final Map map) {
        final VariantIndexer varIndexer = indexers[this.indexer];
        final String[] names = varIndexer.names;
        final int[] indexs = varIndexer.indexs;
        final Object[] varsPool = this.vars;
        for (int i = 0, len = names.length; i < len; i++) {
            map.put(names[i], varsPool[indexs[i]]);
        }
    }

    @Override
    public Function exportFunction(String name) throws NotFunctionException {
        Object func = get(name, false);
        if (!(func instanceof MethodDeclare)) {
            throw new NotFunctionException(func);
        }
        return new Function(this.template, (MethodDeclare) func, this.encoding, this.isByteStream);
    }
}
