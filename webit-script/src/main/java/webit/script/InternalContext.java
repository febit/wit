// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script;

import java.util.HashMap;
import java.util.Map;
import webit.script.core.LoopInfo;
import webit.script.core.VariantIndexer;
import webit.script.exceptions.NotFunctionException;
import webit.script.exceptions.ScriptRuntimeException;
import webit.script.io.Out;
import webit.script.lang.InternalVoid;
import webit.script.lang.KeyValues;
import webit.script.lang.MethodDeclare;
import webit.script.resolvers.GetResolver;
import webit.script.resolvers.OutResolver;
import webit.script.resolvers.ResolverManager;
import webit.script.resolvers.SetResolver;
import webit.script.util.ClassMap;
import webit.script.util.KeyValuesUtil;

/**
 *
 * @author zqq90
 */
public final class InternalContext implements Context {

    public final Template template;
    public final KeyValues rootParams;

    public final Object[] vars;
    public final Object[][] parentScopes;

    public final VariantIndexer[] indexers;
    public int indexer;

    public Out out;
    public boolean isByteStream;
    public String encoding;

    private Object returned;
    private int label;
    private int loopType;

    private Map<Object, Object> locals;
    private InternalContext localContext;

    private final ResolverManager resolverManager;
    private final ClassMap<OutResolver> outters;
    private final ClassMap<GetResolver> getters;
    private final ClassMap<SetResolver> setters;

    public InternalContext(final Template template, final Out out, final KeyValues rootParams, final VariantIndexer[] indexers, final int varSize, final Object[][] parentScopes) {
        this.template = template;
        this.out = out;
        this.rootParams = rootParams;

        this.encoding = out.getEncoding();
        this.isByteStream = out.isByteStream();

        ResolverManager resolverMgr = template.engine.getResolverManager();
        this.resolverManager = resolverMgr;
        this.outters = resolverMgr.outters;
        this.getters = resolverMgr.getters;
        this.setters = resolverMgr.setters;

        this.indexers = indexers;
        this.indexer = 0;
        this.vars = new Object[varSize];
        this.parentScopes = parentScopes;
        rootParams.exportTo(this);
    }

    public InternalContext createSubContext(VariantIndexer[] indexers, InternalContext localContext, int varSize) {
        Object[][] scopes;
        Object[][] myParentScopes = this.parentScopes;
        if (myParentScopes == null) {
            scopes = new Object[][]{this.vars};
        } else {
            scopes = new Object[myParentScopes.length + 1][];
            scopes[0] = this.vars;
            System.arraycopy(myParentScopes, 0, scopes, 1, myParentScopes.length);
        }
        InternalContext newContext = new InternalContext(template, localContext.out, KeyValuesUtil.EMPTY_KEY_VALUES, indexers, varSize, scopes);
        newContext.localContext = localContext;
        return newContext;
    }

    public InternalContext createPeerContext(Template template, VariantIndexer[] indexers, int varSize) {

        InternalContext newContext = new InternalContext(template, this.out, KeyValuesUtil.EMPTY_KEY_VALUES, indexers, varSize, null);
        newContext.localContext = this;
        return newContext;
    }

    public boolean matchLabel(int label) {
        return this.label == 0 || this.label == label;
    }

    public void breakLoop(int label) {
        this.label = label;
        this.loopType = LoopInfo.BREAK;
    }

    public void continueLoop(int label) {
        this.label = label;
        this.loopType = LoopInfo.CONTINUE;
    }

    public void returnLoop(Object value) {
        this.returned = value;
        this.label = 0;
        this.loopType = LoopInfo.RETURN;
    }

    public void resetLoop() {
        this.returned = null;
        this.label = 0;
        this.loopType = 0;
    }

    public void resetBreakLoopIfMatch(int label) {
        if (this.loopType == LoopInfo.BREAK && (this.label == 0 || this.label == label)) {
            this.resetLoop();
        }
    }

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

    @Override
    public void set(final String name, final Object value) {
        int index = indexers[this.indexer].getIndex(name);
        if (index >= 0) {
            this.vars[index] = value;
        }
    }

    public void outNotNull(final byte[] bytes) {
        this.out.write(bytes);
    }

    public void outNotNull(final char[] chars) {
        this.out.write(chars);
    }

    public Object getBeanProperty(final Object bean, final Object property) {
        if (bean != null) {
            final GetResolver resolver;
            if ((resolver = this.getters.unsafeGet(bean.getClass())) != null) {
                return resolver.get(bean, property);
            }
        }
        return this.resolverManager.get(bean, property);
    }

    public void setBeanProperty(final Object bean, final Object property, final Object value) {
        if (bean != null) {
            final SetResolver resolver;
            if ((resolver = this.setters.unsafeGet(bean.getClass())) != null) {
                resolver.set(bean, property, value);
                return;
            }
        }
        this.resolverManager.set(bean, property, value);
    }

    public void out(final Object obj) {
        if (obj != null) {
            final Class type;
            if ((type = obj.getClass()) == String.class) {
                this.out.write((String) obj);
                return;
            }
            final OutResolver resolver;
            if ((resolver = this.outters.unsafeGet(type)) != null) {
                resolver.render(this.out, obj);
                return;
            }
            this.resolverManager.resolveOutResolver(type).render(this.out, obj);
        }
    }

    @Override
    public Object getLocal(final Object name) {
        if (localContext != null) {
            return localContext.getLocal(name);
        }
        final Map<Object, Object> map;
        if ((map = this.locals) != null) {
            return map.get(name);
        }
        return null;
    }

    @Override
    public void setLocal(final Object name, final Object value) {
        if (localContext != null) {
            localContext.setLocal(name, value);
            return;
        }
        final Map<Object, Object> map;
        if ((map = this.locals) != null) {
            map.put(name, value);
            return;
        }
        (this.locals = new HashMap<>()).put(name, value);
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
    public Function exportFunction(String name) throws NotFunctionException {
        Object func = get(name, false);
        if (func instanceof MethodDeclare) {
            return new Function(this.template, (MethodDeclare) func, this.encoding, this.isByteStream);
        }
        throw new NotFunctionException(func);
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
}
