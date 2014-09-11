// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script;

import java.util.HashMap;
import java.util.Map;
import webit.script.core.LoopInfo;
import webit.script.core.VariantIndexer;
import webit.script.exceptions.NotFunctionException;
import webit.script.exceptions.ScriptRuntimeException;
import webit.script.io.Out;
import webit.script.lang.KeyValueAccepter;
import webit.script.lang.KeyValues;
import webit.script.lang.MethodDeclare;
import webit.script.lang.Void;
import webit.script.resolvers.GetResolver;
import webit.script.resolvers.OutResolver;
import webit.script.resolvers.ResolverManager;
import webit.script.resolvers.SetResolver;
import webit.script.util.ClassMap;

/**
 *
 * @author Zqq
 */
public final class Context implements KeyValueAccepter {

    public static final Void VOID = new Void();

    public Template template;
    public KeyValues rootParams;

    public Object[] vars;
    public VariantIndexer[] indexers;
    public int indexer;

    public Out out;
    public boolean isByteStream;
    public String encoding;

    private Object returned;
    private int label;
    private int loopType;

    private Map<Object, Object> locals;

    private final ResolverManager resolverManager;
    private final ClassMap<OutResolver> outters;
    private final ClassMap<GetResolver> getters;
    private final ClassMap<SetResolver> setters;

    public Context(final Template template, final Out out, final KeyValues rootParams) {
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
    }

    public void pushRootParams() {
        rootParams.exportTo(this);
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
        Object result = this.loopType == LoopInfo.RETURN ? this.returned : VOID;
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

    public void set(final String key, final Object value) {
        int index = indexers[this.indexer].getIndex(key);
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

    public Object getBean(final Object bean, final Object property) {
        if (bean != null) {
            final GetResolver resolver;
            if ((resolver = this.getters.unsafeGet(bean.getClass())) != null) {
                return resolver.get(bean, property);
            }
        }
        return this.resolverManager.get(bean, property);
    }

    public void setBean(final Object bean, final Object property, final Object value) {
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

    public Object getLocal(final Object key) {
        final Map<Object, Object> map;
        if ((map = this.locals) != null) {
            return map.get(key);
        }
        return null;
    }

    public void setLocal(final Object key, final Object value) {
        final Map<Object, Object> map;
        if ((map = this.locals) != null) {
            map.put(key, value);
            return;
        }
        (this.locals = new HashMap<Object, Object>()).put(key, value);
    }

    public Object[] get(final String[] keys) {
        int i;
        final Object[] results = new Object[i = keys.length];
        while (i != 0) {
            --i;
            results[i] = get(keys[i], true);
        }
        return results;
    }

    public Object get(final String key) {
        return get(key, true);
    }

    public Object get(final String key, boolean force) {
        int index = indexers[this.indexer].getIndex(key);
        if (index >= 0) {
            return this.vars[index];
        }
        if (force) {
            throw new ScriptRuntimeException("Not found variant named:".concat(key));
        }
        return null;
    }

    /**
     * Export a named function.
     *
     * @since 1.5.0
     * @param name
     * @return
     * @throws NotFunctionException
     */
    public Function exportFunction(String name) throws NotFunctionException {
        Object func = get(name, false);
        if (func instanceof MethodDeclare) {
            return new Function(this.template, (MethodDeclare) func, this.encoding, this.isByteStream);
        }
        throw new NotFunctionException(func);
    }

    /**
     * Export vars to a given map.
     *
     * @param map
     */
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
